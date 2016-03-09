package Game;

import java.util.Arrays;

public class Steen {

	private int kleur;
	private int vorm;
	
    public Steen(int vorm, int kleur) throws InvalidArgumentException {
        if (vorm >= 0 && vorm < 6 && kleur >= 0 && kleur < 6) {
        	this.vorm = vorm;
            this.kleur = kleur;
        }
        else {
        	throw new InvalidArgumentException();
        }
    }
    
    public Steen(String vormenkleur) throws InvalidArgumentException {
    	if (vormenkleur.length() == 3) {
    		vorm = vormenkleur.charAt(0);
    		kleur = vormenkleur.charAt(2);
    	}
    	else {
    		throw new InvalidArgumentException();
    	}
    }
    
	public int[] getType() {
		int[] typesteen;
		typesteen = new int[2];
		typesteen[0] = this.vorm;
		typesteen[1] = this.kleur;		
		return typesteen;
	}
	
	public boolean equals(Steen steen) {
		if (Arrays.equals(steen.getType(), this.getType())) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
