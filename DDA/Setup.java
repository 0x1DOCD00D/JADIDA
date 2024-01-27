import com.sun.jdi.VirtualMachine;
import java.io.File;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.*;

import java.util.Map;
import java.io.IOException;
import java.util.List;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Setup {
	
		private static void runProcess(String command) throws Exception {
		    Process pro = Runtime.getRuntime().exec(command);
		    pro.waitFor();
		   // System.out.println(command + " exitValue() " + pro.exitValue());
		}
		// TODO take care of global variables	
	
	
		public static void main(String[] args) throws IOException {	   
		AstVisitor visitor = new AstVisitor(null, true);
	    visitor.run();
	    String output_filepath = visitor.get_output_filepath();
	    String compile_cmd = "javac ";
	    compile_cmd += output_filepath;	    
	    try {
	        runProcess(compile_cmd);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	   
		LaunchingConnector conn = null;
		// Connector is used to establish connection between a debugger
		// and a target VM
		List<Connector> connectors = Bootstrap.virtualMachineManager().allConnectors();
        for (Connector connector : connectors) {
        	// Find a launching connector as it launches a VM before connecting to it
            if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
                conn = (LaunchingConnector)connector;
            }
        }
        if(conn == null)
        	throw new Error("No launching connector");
        
        Map<String, Connector.Argument> arguments = conn.defaultArguments();
        // Name of the main function that needs to be loaded
        Connector.Argument mainArg = (Connector.Argument)arguments.get("main");
        mainArg.setValue("test");
        // Add the classpath option to the launched vm as it does not where to 
        // search for the main class added in the mainArg
        Connector.Argument options =(Connector.Argument)arguments.get("options");
        File f = null;
        String currentDir = System.getProperty("user.dir");
        System.out.println(currentDir);
        String option_val;
        option_val = "-cp " + currentDir + "\\src";
        options.setValue(option_val);
        

        try {
        	VirtualMachine vm = conn.launch(arguments);
        	
        	EventManager mgr = new EventManager(vm);
            mgr.setEventRequests();
            mgr.start();
            
        	
        	// If a target VM is launched through this function, its output and 
        	// error streams must be read as it executes. These streams are available 
        	// through the Process object returned by VirtualMachine.process(). 
        	// If the streams are not periodically read, the target VM will stop executing
        	// when the buffers for these streams are filled.
            Process process = vm.process();
            
            // Copy target's output and error to our output and error.
            StreamRedirectThread errThread = new StreamRedirectThread("error reader",
                                                 process.getErrorStream(),
                                                 System.err);
            StreamRedirectThread outThread = new StreamRedirectThread("output reader",
                                                 process.getInputStream(),
                                                 System.out);
            errThread.start();
            outThread.start();
        	vm.resume();
        } catch (IOException exc) {
            throw new Error("Unable to launch target VM: " + exc);
        } catch (IllegalConnectorArgumentsException exc) {
            throw new Error("Internal error: " + exc);
        } catch (VMStartException exc) {
            throw new Error("Target VM failed to initialize: " +
                            exc.getMessage());
        }
    }
}
