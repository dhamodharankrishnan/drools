package com.mycompany.ruleengine.sample;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.impl.GenericCommand;
import org.kie.api.KieServices;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;

public class SampleKieClient {
	
	public static void main(String args[]){
		
		System.out.println("Sample Kie Client..");
		KieServicesConfiguration conf;
		KieServicesClient kieServicesClient;
		
		String kieClientURL = "http://localhost:9080/kie-server/services/rest/server";
		String kieClientUser= "kieserver";
		String kieClientPWD = "kieserver1!";
		String kieContainer = "SampleContainer";
		
		conf = KieServicesFactory.newRestConfiguration(kieClientURL, kieClientUser, kieClientPWD);
		conf.setMarshallingFormat(MarshallingFormat.JSON);
		conf.setTimeout(1000);
		
		kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
		
        RuleServicesClient ruleClient = kieServicesClient.getServicesClient(RuleServicesClient.class);
        
        List<GenericCommand<?>> commands = new ArrayList<GenericCommand<?>>();
        
        SampleRequest sampleRequest = new SampleRequest();

        SamplePojo samplePojo = new SamplePojo();
        samplePojo.setSubBool(true);
        samplePojo.setSuperBool(true);
        samplePojo.setThirdBool(true);
        
        sampleRequest.setState("Begin");
        sampleRequest.getOfferList().add(samplePojo);
        
        KieCommands kieCommands = KieServices.Factory.get().getCommands();
        
        SampleResponse sampleResponse = new SampleResponse();
        
        //Request
      commands.add((GenericCommand<?>) kieCommands.newInsert(sampleRequest, "sampleRequest"));
      sampleResponse.getResponseList().clear();
      commands.add((GenericCommand<?>) kieCommands.newInsert(sampleResponse, "sampleResponse"));
      
//        commands.add((GenericCommand<?>) kieCommands.newInsert(samplePojo, "samplePojo"));
        
        
        //Fire all rules
        commands.add((GenericCommand<?>) kieCommands.newFireAllRules());
        
        //Preparing the batch Execution command.
        BatchExecutionCommand batchCommand = kieCommands.newBatchExecution(commands);
        
        
        
        //Execution of the command.
        ServiceResponse<ExecutionResults> responseWithResults = 
        		ruleClient.executeCommandsWithResults(kieContainer, batchCommand);
        
        //Retrieve the results.
        ExecutionResults execResults = responseWithResults.getResult();
        SampleResponse sampleResponseOutput = (SampleResponse) execResults.getValue("sampleResponse");
        
        
        System.out.println("Rule Output"+sampleResponseOutput);
        System.out.println("No of recs:"+sampleResponseOutput.getResponseList().size());
        
        
	}

}
