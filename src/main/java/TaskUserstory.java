import java.util.ArrayList;
import java.util.List;

public class TaskUserstory {

	private String userstory;
	private String userstoryAddress;
	private List<Task> tasks = new ArrayList<Task>();
	
	public TaskUserstory(){}	
	
	public TaskUserstory(String userstory, String userstoryAddress, List<Task> tasks) {
		super();
		this.userstory = userstory;
		this.userstoryAddress = userstoryAddress;
		this.tasks = tasks;
	}
	
	public String getUserstory() {
		return userstory;
	}
	public void setUserstory(String userstory) {
		this.userstory = userstory;
	}
	public List<Task> getTasks() {
		return tasks;
	}
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	public String getUserstoryAddress() {
		return userstoryAddress;
	}
	public void setUserstoryAddress(String userstoryAddress) {
		this.userstoryAddress = userstoryAddress;
	}
}
