package Transport;
import Utilities.Constants;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.Random;
import java.util.StringTokenizer;


public class offerTransport extends ContractNetResponder {

    String init_position = null;
    String dest_position = null;

    public offerTransport(Agent a, MessageTemplate mt){
        super(a, mt);
    }

    /*
    Propose msg: halt time
     */

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage msg = cfp.createReply();

        StringTokenizer content = new StringTokenizer(cfp.getContent(), Constants.TOKEN);
        init_position = content.nextToken();
        dest_position = content.nextToken();
        int objectWeight = Integer.parseInt(content.nextToken());

        if((((TransportAgent)myAgent).payload > objectWeight) && !(((TransportAgent)myAgent).reserved)) {

            msg.setPerformative(ACLMessage.PROPOSE);
            Random rand = new Random();
            String haltTime = Integer.toString(rand.nextInt(100) + 1); //the time that the AGV has been without doing any transportation
            msg.setContent(haltTime);
        } else {
            msg.setPerformative(ACLMessage.REFUSE);
        }
        return msg;
    }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
            ACLMessage msg = cfp.createReply();
            if(init_position.equals(dest_position)) {
                msg.setPerformative(ACLMessage.INFORM);
                msg.setContent("false");
            }else{
                ((TransportAgent)myAgent).reserved = true;
                System.out.println(cfp.getSender().getLocalName() + ": " + myAgent.getLocalName() + " Doing transportation from " + init_position + " to " + dest_position);
                //block(2000);
                ((TransportAgent)myAgent).myLib.executeMove(init_position, dest_position, propose.getSender().getLocalName());
                System.out.println(cfp.getSender().getLocalName() + ": " + myAgent.getLocalName() + " transportation Done");
                msg.setPerformative(ACLMessage.INFORM);
                msg.setContent("true");
                ((TransportAgent)myAgent).reserved = false;
            }
            return msg;
        }

}

