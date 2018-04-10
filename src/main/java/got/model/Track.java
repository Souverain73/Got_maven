package got.model;

import java.util.Arrays;

public class Track {
	private int id;
	private String name;
	private Fraction[] data;
	
	public Track(int id, String name, Fraction[] data){
		this.id = id;
		this.name = name;
		this.data = data;
	}
	
	public int getPos(Fraction fraction){
		for (int i=0; i<data.length; i++){
			if (data[i] == fraction) return i;
		}
		return -1;
	}
	
	/**
	 * Compare positions of 2 fractions on track
	 * @param one
	 * @param another
	 * @return 1 - if one is first <br>
	 * -1 - if another is first and<br>
	 *  0 - if can't find this fractions on track;
	 */
	public int compare(Fraction one, Fraction another){
		for (int i=0; i<data.length; i++){
			if (data[i] == one){
				return 1;
			}else if (data[i] == another){
				return -1;
			}
		}
		return 0;
	}
	
	public Fraction[] getData(){
		return data;
	}
	
	public void setData(Fraction[] data){
		this.data = data;
	}
	
	public Fraction getNext(Fraction fraction){
		return data[(getPos(fraction)+1)%data.length];
	}
	
	public Fraction getFirst(){
		return data[0];
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Track{" +
				"name='" + name + '\'' +
				", data=" + Arrays.toString(data) +
				'}';
	}
}
