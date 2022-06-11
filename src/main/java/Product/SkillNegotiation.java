package Product;

import Utilities.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;

public class SkillNegotiation extends ContractNetInitiator {

    ACLMessage msg;
    public SkillNegotiation(Agent a, ACLMessage msg) {
        super(a, msg);
        this.msg = msg;
    }

    @Override
    protected void handleAllResponses (Vector response, Vector acceptances) {
        int best_resource = getBestOffer (response, acceptances);
        if (best_resource != -1) {
        sendDecision(best_resource, response, acceptances);
        }
        if (best_resource == -1) { //no proposals

            for (int i = 0; i< response.size(); i++) {
                ACLMessage msg_aux = (ACLMessage) response.get(i);
                ACLMessage reply = msg_aux.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                myAgent.send(reply);
            }

            Vector<ACLMessage> newIt = new Vector<ACLMessage>();
            newIt.add(this.msg);
            for(int i = 0; i<300000; i++) //the block(1000) doesn't work in here, but this one kinda works
                block(10);
            this.reset(msg);
            //this.newIteration(newIt);
        }
    }

    private int getBestOffer (Vector response, Vector acceptances) {
        int best_proposal = 0;
        int best_resource = -1;
        int best_freq = 0;
        int numberOfProposals = 0;
        int numberOfRefusals = 0;
        String[] occupiedResources = new String [10];
        int occupiedResourcesIndex = 0;

        //choose the best proposal
        for (int i = 0; i<response.size(); i++) {
            ACLMessage msg_received = (ACLMessage)response.get(i);

            if (msg_received.getPerformative() == ACLMessage.PROPOSE) { //if their response is a proposition

                StringTokenizer content = new StringTokenizer(msg_received.getContent(), Constants.TOKEN);
                String performance_value = content.nextToken();
                String location = content.nextToken();
                String skill1 = content.nextToken();
                String skill2;

                int freq = Collections.frequency(((ProductAgent)myAgent).executionPlan,skill1);
                if(content.hasMoreElements()) {
                    skill2 = content.nextToken();
                    freq = freq + Collections.frequency(((ProductAgent)myAgent).executionPlan,skill2);
                }

                numberOfProposals = numberOfProposals +1;

                int proposal_value = Integer.parseInt(performance_value);
                 if (best_proposal == 0) {
                     best_proposal = proposal_value;
                     best_resource = i;
                     best_freq = freq;
                     ((ProductAgent)myAgent).nextLocation = location;
                 }
                 if (freq > best_freq) {
                     best_resource = i;
                     best_proposal = proposal_value;
                     best_freq = freq;
                     ((ProductAgent)myAgent).nextLocation = location;
                 }
                 if (freq == best_freq && proposal_value < best_proposal) {
                     best_resource = i;
                     best_proposal = proposal_value;
                     best_freq = freq;
                     ((ProductAgent)myAgent).nextLocation = location;
                 }
            }
            if (msg_received.getPerformative() == ACLMessage.REFUSE) {
                occupiedResources[numberOfRefusals] = msg_received.getContent();
                numberOfRefusals = numberOfRefusals + 1;
            }

            if ( ( numberOfProposals == 1) && ( best_freq != 2 ) && (numberOfRefusals > 0) ) { //to prevent the case where both products on the resources want to  each others resources (blocking the plant)
                if ( (RefusalsHasBestOffer(occupiedResources,((ProductAgent)myAgent).productType,best_freq) > 0) )
                    best_resource = -1;
            }

        }
        return best_resource;
    }

    private void sendDecision (int best_resource, Vector response, Vector acceptances) {

        for (int i = 0; i < response.size(); i++) {
            ACLMessage msg = (ACLMessage) response.get(i);
            ACLMessage reply = msg.createReply();

            if (i == best_resource) { // SEND ACCEPT
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent(((ProductAgent)myAgent).productType);
            }
            else // SEND REFUSE
                reply.setPerformative((ACLMessage.REJECT_PROPOSAL));

            acceptances.add(reply);
        }


        ((ProductAgent) myAgent).skillReserved = true;

        String agentName = ((ACLMessage) response.get(best_resource)).getSender().getLocalName();

        ((ProductAgent) myAgent).pastSkillReservedFrom = ((ProductAgent) myAgent).currentSkillReservedFrom;
        ((ProductAgent) myAgent).currentSkillReservedFrom = agentName;

        ((ProductAgent) myAgent).msgExecuteSkill.addReceiver(new AID(agentName, false));
        ((ProductAgent) myAgent).msgExecuteSkill.setContent(((ProductAgent) myAgent).currentSkill);
        ((ProductAgent) myAgent).msgExecuteSkill.setOntology(Constants.ONTOLOGY_EXECUTE_SKILL);

    }

    private int RefusalsHasBestOffer (String[] string, String ProductType, int best_freq) {
        int bestOffers = 0;
        for(int i = 0; i < string.length; i++) {
            if (string[i] != null) {
                StringTokenizer content = new StringTokenizer(string[i], Constants.TOKEN);
                String stringProductType = content.nextToken();
                String skill1 = content.nextToken();
                String skill2;

                int freq = Collections.frequency(((ProductAgent)myAgent).executionPlan,skill1);
                if(content.hasMoreElements()) {
                    skill2 = content.nextToken();
                    freq = freq + Collections.frequency(((ProductAgent)myAgent).executionPlan,skill2);
                }

                if (!stringProductType.equals(ProductType)) { //if the resource is NOT working on the same product type
                    if(freq == 2) //and it can do both skills needed
                        bestOffers = bestOffers + 1;
                }
            }
        }
        return bestOffers;
    }
}
