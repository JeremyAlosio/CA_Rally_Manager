import java.io.IOException;

import com.google.gson.JsonObject;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;

public class Task {

	private String userstory;
	private String taskName;
	private String owner;
	private String description;
	private String project;
	private String timeEstimate;
	private String timeToDo;
	private String timeActuals;
	private String uploadAddress;
	
	public Task(){};
	
	public Task(String userstory, String taskName, String description, String owner, String project, String timeEstimate, String timeToDo, String timeActuals) {
		super();
		this.userstory = userstory;
		this.taskName = taskName;
		this.description = description;
		this.owner = owner;
		this.project = project;
		this.timeEstimate = timeEstimate;
		this.timeToDo = timeToDo;
		this.timeActuals = timeActuals;
	}
	
	public void UploadTask()
	{
		JsonObject newTask = new JsonObject();
		
		newTask.addProperty("Name", taskName);
		newTask.addProperty("State", "Defined");
		newTask.addProperty("Description", description);
		newTask.addProperty("Owner", owner);
		newTask.addProperty("ToDo", timeToDo);
		newTask.addProperty("Estimate", timeEstimate);
		newTask.addProperty("Actuals", timeActuals);
		newTask.addProperty("WorkProduct", project);
		
		
		CreateRequest taskCreateRequest = new CreateRequest("task", newTask);
		CreateResponse taskCreateResponse = null;
		try {
			taskCreateResponse = RallyMassUploaderGui.restApi.create(taskCreateRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (taskCreateResponse.wasSuccessful()) {
			RallyMassUploaderGui.statusTextArea.setText(RallyMassUploaderGui.statusTextArea.getText() + "\n" + taskName + " for " + userstory + " has been uploaded for user " + owner);
			uploadAddress = taskCreateResponse.getObject().getAsJsonObject().get("_ref").getAsString();
		} 
		
	}
	
	public void DeleteTask()
	{
		DeleteRequest deleteRequest = new DeleteRequest(uploadAddress);
		DeleteResponse deleteResponse = null;
		
		// ------------------------------------------------------

		try {
			deleteResponse = RallyMassUploaderGui.restApi.delete(deleteRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (deleteResponse.wasSuccessful()) {
			RallyMassUploaderGui.statusTextArea.setText(RallyMassUploaderGui.statusTextArea.getText() + "\n" + uploadAddress + " was removed successfully.");
		}
	}
	
	public String getUserstory() {
		return userstory;
	}

	public void setUserstory(String userstory) {
		this.userstory = userstory;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTimeEstimate() {
		return timeEstimate;
	}

	public void setTimeEstimate(String timeEstimate) {
		this.timeEstimate = timeEstimate;
	}

	public String getTimeToDo() {
		return timeToDo;
	}

	public void setTimeToDo(String timeToDo) {
		this.timeToDo = timeToDo;
	}


	public String getTimeActuals() {
		return timeActuals;
	}

	public void setTimeActuals(String timeActuals) {
		this.timeActuals = timeActuals;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUploadAddress() {
		return uploadAddress;
	}

	public void setUploadAddress(String uploadAddress) {
		this.uploadAddress = uploadAddress;
	}
}
