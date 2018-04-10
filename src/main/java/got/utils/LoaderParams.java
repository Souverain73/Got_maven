package got.utils;

import java.util.HashMap;

public class LoaderParams extends HashMap<String, Object> {

	public LoaderParams(){
		super();
	}
	
	public LoaderParams(String [] keyValues){
		super();
		for (int i=0; i*2+1<keyValues.length; i++){
			put(keyValues[i*2], keyValues[i*2+1]);
		}
	}
	
	
	@Override
	public Object put(String arg0, Object arg1){
		if (arg1 == null){
			return null;
		}
		return super.put(arg0, arg1);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.keySet().forEach((key)-> sb.append(key).append(":").append(this.get(key)));
		return sb.toString();
	}
	
	private static final long serialVersionUID = 1L;
}
