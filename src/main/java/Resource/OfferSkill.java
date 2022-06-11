package Resource;

import Utilities.Constants;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;

import java.util.Random;

public class OfferSkill extends ContractNetResponder {

    /*
    Propose msg: timeProduction + TOKEN + location + TOKEN + associatedSkills (separated with TOKEN)
    Refuse msg:  reservedToProductType + associatedSkills (separated with TOKEN)
     */

    public OfferSkill(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp (ACLMessage cfp) {
        ACLMessage msg = cfp.createReply();

        if ( (cfp.getSender().equals(((ResourceAgent)myAgent).reservedTo)) || ((ResourceAgent)myAgent).reservedTo == null) { //if the resource agent is not yet reserved

            ((ResourceAgent)myAgent).reservedSkill = cfp.getContent();
            msg.setPerformative(ACLMessage.PROPOSE);
            Random rand = new Random();

            StringBuilder infoSkills = new StringBuilder();
            for (String s : ((ResourceAgent)myAgent).associatedSkills ) {
                if (!infoSkills.toString().equals(""))
                    infoSkills.append(Constants.TOKEN);
                infoSkills.append(s);
            }
            String timeProduction = Integer.toString(rand.nextInt(100) + 1);
            msg.setContent(timeProduction + Constants.TOKEN + ((ResourceAgent)myAgent).location + Constants.TOKEN + infoSkills); //sends a random value (between 1 and 100) considered as the time (in sec's) it takes to do a certain skill
        }
        else {
            StringBuilder info = new StringBuilder();

            info.append(((ResourceAgent)myAgent).reservedToProductType);

            for (String s : ((ResourceAgent)myAgent).associatedSkills ) {
                info.append(Constants.TOKEN);
                info.append(s);
            }
            msg.setContent("" + info);
            msg.setPerformative(ACLMessage.REFUSE);
        }
        return msg;
    }

    @Override
    protected ACLMessage handleAcceptProposal (ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        ((ResourceAgent)myAgent).reserved = true;
        ((ResourceAgent)myAgent).reservedTo = accept.getSender();
        ((ResourceAgent)myAgent).reservedToProductType = accept.getContent();
        ACLMessage msg = cfp.createReply();
        msg.setPerformative(ACLMessage.INFORM);
        return msg;
    }

}
