package Product;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;

/**
 *
 * @author Ricardo Silva Peres <ricardo.peres@uninova.pt>
 */
public class ProductAgent extends Agent {    
    
    String id;
    ArrayList<String> executionPlan = new ArrayList<>();
    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
    ACLMessage msgExecuteSkill = new ACLMessage(ACLMessage.REQUEST);
    ACLMessage msgInformRes = new ACLMessage(ACLMessage.REQUEST);
    String currentSkill = null;
    String currentLocation = "Source"; //this has to be changed, it limits the number of sources
    String nextLocation = null;
    boolean skillReserved = false;
    String pastSkillReservedFrom = null;
    String currentSkillReservedFrom = null;
    boolean skillDone = false;
    int objectWeight = 6;
    String productType;
    
    @Override
    protected void setup() {
        Object[] args = this.getArguments();
        this.id = (String) args[0];
        this.executionPlan = this.getExecutionList((String) args[1]);

        this.productType = (String) args[1];

        System.out.println("Product launched: " + this.id + " Requires: " + executionPlan);
        SequentialBehaviour sb = new SequentialBehaviour();
        for (String s : executionPlan) {
            sb.addSubBehaviour(new newExecPlanStep(s));
            sb.addSubBehaviour(new GetSkillfulAgent());
            sb.addSubBehaviour(new SkillNegotiation(this, cfp));
            sb.addSubBehaviour(new GetSkillfulAgent());
            sb.addSubBehaviour(new getTransport(this, cfp));
            sb.addSubBehaviour(new InformResourceClear(this, msgInformRes));
            sb.addSubBehaviour(new SkillExecutionRequest(this, msgExecuteSkill));
        }
        sb.addSubBehaviour(new InformResourceClear(this, msgInformRes));
        sb.addSubBehaviour(new DestroyProduct());
        this.addBehaviour(sb);
    }

    @Override
    protected void takeDown() {
        super.takeDown(); //To change body of generated methods, choose Tools | Templates.
    }
    
    private ArrayList<String> getExecutionList(String productType){
        return switch (productType) {
            case "A" -> Utilities.Constants.PROD_A;
            case "B" -> Utilities.Constants.PROD_B;
            case "C" -> Utilities.Constants.PROD_C;
            default -> null;
        };
    }
    
}
