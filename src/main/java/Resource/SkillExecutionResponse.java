package Resource;

import Utilities.Constants;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import pt.unl.fct.srcim.lab3.InspectionModel;

import java.util.Collections;
import java.util.List;

public class SkillExecutionResponse extends AchieveREResponder {

    public SkillExecutionResponse(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleRequest (ACLMessage request) {

        ACLMessage msg = request.createReply();
        String skill = request.getContent();

        if (Collections.frequency(List.of((((ResourceAgent) myAgent).associatedSkills)), skill) == 1) {
            msg.setPerformative(ACLMessage.AGREE);
        } else {
            msg.setPerformative(ACLMessage.REFUSE);
        }
        return msg;
    }

    @Override
    protected ACLMessage prepareResultNotification (ACLMessage request, ACLMessage response) {
        ACLMessage msg = request.createReply();
        System.out.println(request.getSender().getLocalName() + ": " + myAgent.getLocalName() + " is STARTING the skill: " + ((ResourceAgent) myAgent).reservedSkill);
        ((ResourceAgent) myAgent).myLib.executeSkill(((ResourceAgent) myAgent).reservedSkill);
        //block(5000);
        if(((ResourceAgent) myAgent).reservedSkill.equals(Constants.SK_QUALITY_CHECK)) {
            InspectionModel inspector = new InspectionModel("srcim_model_9625.h5");
            //NOT OK test path
            String path = "C:\\Users\\danie\\Desktop\\Faculdade\\SRCIM\\Pratica\\3\\srcim2022lab3\\images\\product_DEFECT.jpg";
            //OK test path
            //String path = "C:\\Users\\danie\\Desktop\\Faculdade\\SRCIM\\Pratica\\3\\srcim2022lab3\\images\\product_OK.jpg";
            // Correct functioning path
            //String path = "C:\\Users\\danie\\Desktop\\Faculdade\\SRCIM\\Pratica\\3\\srcim2022lab3\\images\\" + myAgent.getLocalName() + ".jpg";
            int result = inspector.predict(inspector.loadImage(path, 512, 512, 3)); //result = 1 means that it's NOT OK

            msg.setPerformative(ACLMessage.INFORM); //FOR NOW!! JUST FOR TESTING
            msg.setOntology(Constants.ONTOLOGY_QUALITY_CHECK);
            msg.setContent(String.valueOf(result));
        } else {
            msg.setPerformative(ACLMessage.INFORM);
        }
        return msg;
    }
}
