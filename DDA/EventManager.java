import com.sun.jdi.*;
import com.sun.jdi.request.*;
import com.sun.jdi.event.*;

import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;


public class EventManager extends Thread {
	
	 // java library files which we want to exclude for generating any events
	
	
	private String[] excludes = {"java.*", "javax.*", "sun.*",
                                 "com.sun.*"};

	
	
	private final VirtualMachine vm;   // Running VM
	private boolean connected = true;
	Stack<Set<String>> names = new Stack<Set<String>>();
	HashMap<String, Set<String>> expression_map = new HashMap<String, Set<String>>();
	List<Field> watched_fields = new LinkedList<Field>();
	
	EventManager(VirtualMachine vm) {
		this.vm = vm;
	}
	
	private void decompile_expr_map() {
        Iterator it = expression_map.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	    	System.out.println("Modified Field");
	    	System.out.println(pair.getKey());
	    	System.out.println("Accessed Field");
	    	Set<String> value = (Set<String>)pair.getValue();
	    	for(String s : value) {
	    		System.out.println(s);
	    	}
	    	System.out.println("##################################");
	    
	    }
	}
	
	public void run() {
		  EventQueue queue = vm.eventQueue();
	      while (connected) {
	    	  try {
	    		  EventSet eventSet = queue.remove();
	              EventIterator it = eventSet.eventIterator();
	              while (it.hasNext()) {
	            	  handleEvent(it.nextEvent());
	              }
	              eventSet.resume();
	          } catch (InterruptedException exc) {
	                // Ignore
	          } catch (VMDisconnectedException discExc) {
	                //handleDisconnectedException();
	                break;
	          }
	     }
	     AstVisitor visitor = new AstVisitor(expression_map, false);
		 visitor.run();
	     decompile_expr_map();
		
	}
	
	
	
	public void setEventRequests() {
		EventRequestManager mgr = vm.eventRequestManager();
		//if(ev == Events.E_WATCH_ACCESS_FIELDS || ev == Events.E_WATCH_MODIFY_FIELDS) {
		ClassPrepareRequest cpr = mgr.createClassPrepareRequest();
		for (int i=0; i<excludes.length; ++i) {
			cpr.addClassExclusionFilter(excludes[i]);
	    }
        cpr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        cpr.enable();
        MethodEntryRequest mtr = mgr.createMethodEntryRequest();
		for (int i=0; i<excludes.length; ++i) {
			mtr.addClassExclusionFilter(excludes[i]);
	    }
        mtr.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        mtr.enable();
        MethodExitRequest mer = mgr.createMethodExitRequest();
		for (int i=0; i<excludes.length; ++i) {
			mer.addClassExclusionFilter(excludes[i]);
		}
        mer.setSuspendPolicy(EventRequest.SUSPEND_ALL);
        mer.enable();
		//}
	}
	
	private void handleEvent(Event event) {
        if (event instanceof AccessWatchpointEvent) {
           fieldWatchEvent((AccessWatchpointEvent)event);
        } else if (event instanceof ModificationWatchpointEvent) {
            fieldWatchEvent((ModificationWatchpointEvent)event);
        } else if (event instanceof ClassPrepareEvent) {
            classPrepareEvent((ClassPrepareEvent)event);
        } else if (event instanceof MethodEntryEvent) {
        	methodEntryEvent((MethodEntryEvent)event);
        } else if (event instanceof MethodExitEvent) {
    	    methodExitEvent((MethodExitEvent)event);
        } else if (event instanceof VMDeathEvent) {
          //  vmDeathEvent((VMDeathEvent)event);
        } else if (event instanceof VMStartEvent) {
            //  vmDeathEvent((VMDeathEvent)event);
        } else if (event instanceof VMDisconnectEvent) {
            //vmDisconnectEvent((VMDisconnectEvent)event);
        } else {
            throw new Error("Unexpected event type");
        }
    }
	
	private void fieldWatchEvent(AccessWatchpointEvent event)  {
	    
        Field field = event.field();
        Set<String> names_list = names.peek();
        String accessField = field.name() + "." +  event.location().lineNumber();
     
        names_list.add(accessField);
        //System.out.println("Access Watch Event 1");
		//System.out.println(accessField);
    }
	
	private void fieldWatchEvent(ModificationWatchpointEvent event) {
		
		Field field = event.field();
		Set<String> names_list = names.peek();
		Set<String> names_list_new = new HashSet();
		for(String s : names_list) {
			names_list_new.add(s);
		}
		String modField = field.name() + "." + event.location().lineNumber();
		expression_map.put(modField, names_list_new);
		names_list.clear();		
	}
	
	private void addFieldWatcher(Field field) {
		EventRequestManager mgr = vm.eventRequestManager();
		ModificationWatchpointRequest req_1 =
	               mgr.createModificationWatchpointRequest(field);
	    for (int i=0; i<excludes.length; ++i) {
	    	req_1.addClassExclusionFilter(excludes[i]);
	    }
	    req_1.setSuspendPolicy(EventRequest.SUSPEND_NONE);
	    req_1.enable();
		AccessWatchpointRequest req_2 =
                mgr.createAccessWatchpointRequest(field);
        for (int i=0; i<excludes.length; ++i) {
           req_2.addClassExclusionFilter(excludes[i]);
        }
        req_2.setSuspendPolicy(EventRequest.SUSPEND_NONE);
        req_2.enable();     
		
	}
	
	// This event is triggered because we had created 
	// ClassPrepareRequest which means a new class has been 
	// loaded. Now we gave to add watchpoint request for its fields
	private void classPrepareEvent(ClassPrepareEvent event) {	
        List<Field> fields = event.referenceType().visibleFields();
        for (Field field : fields) {
        	Iterator iter = watched_fields.iterator();
        	boolean watched = false;
        	while(iter.hasNext()) {
        		Field val = (Field)iter.next();
        		if(val.equals(field)) {
        			watched = true;
        		} 
        	}
        	if(!watched) {
        		//System.out.println(field.name());
        		addFieldWatcher(field);
        		watched_fields.add(field);
        	}
        }
	}
	
	private void methodEntryEvent(MethodEntryEvent event)  {
		ThreadReference thread = event.thread();
		Method method = event.method();
		try {
			List<LocalVariable> args = method.variables();
			ListIterator<LocalVariable> it = args.listIterator();
			while(it.hasNext()) {
				LocalVariable lv = it.next();
				System.out.println(lv.name());    
				
			}
			
		} catch(AbsentInformationException ex) {
			
		}
		String method_name = method.name() + ".";
		try {
			List<StackFrame> frames = thread.frames();
			// This step is needed because we need the method call location not the method declaration location
			if(frames.size() > 1) {
			   StackFrame frame = frames.get(1);
			   method_name += frame.location().lineNumber();	
			}
		} catch(IncompatibleThreadStateException ex) {
			
		} 
		if(!names.empty()) {
			Set<String> names_list = names.peek();			
			names_list.add(method_name);
		}
		Set<String> names_list_new = new HashSet();
		names.push(names_list_new);   
    }
	
	private void methodExitEvent(MethodExitEvent event)  {
		names.pop();
    }
	
   

	
}
