package Product;

import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.Vector;

    public class getTransport extends ContractNetInitiator {

        public getTransport(Agent a, ACLMessage msg){
            super(a, msg);
        }

        @Override
        protected void handleInform(ACLMessage inform){
            if ( ((ProductAgent) myAgent).pastSkillReservedFrom != null )
                ((ProductAgent) myAgent).msgInformRes.addReceiver(new AID(((ProductAgent)myAgent).pastSkillReservedFrom,false));
            if(inform.getContent().equals("true")) {//Inform Resource that its free
                ((ProductAgent) myAgent).msgInformRes.setContent("true");
            } else {
                ((ProductAgent) myAgent).msgInformRes.setContent("false");
            }
            ((ProductAgent) myAgent).msgInformRes.setOntology(Constants.ONTOLOGY_CLEAR_RESOURCE);
            ((ProductAgent) myAgent).currentLocation = ((ProductAgent) myAgent).nextLocation;
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances){

            int best_proposal = -1;
            int best_transport = -1;

            //choose the AGV that has been in halt for longer
            for (int i = 0; i<responses.size(); i++) {
                ACLMessage msg = (ACLMessage) responses.get(i);
                if (msg.getPerformative() == ACLMessage.PROPOSE) { //if their response is a proposition
                    int proposal_value = Integer.parseInt(msg.getContent());
                    if (best_proposal == -1) {
                        best_proposal = proposal_value;
                        best_transport = i;
                    }
                    if (proposal_value > best_proposal) {
                        best_transport = i;
                        best_proposal = proposal_value;
                    }
                }
            }
            for (int i = 0; i < responses.size(); i++) {
                ACLMessage msg = (ACLMessage) responses.get(i);
                ACLMessage reply = msg.createReply();
                if (i == best_transport)  // SEND ACCEPT
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                else // SEND REFUSE
                    reply.setPerformative((ACLMessage.REJECT_PROPOSAL));
                acceptances.add(reply);
            }
        }
    }