import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.DeleteRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.DeleteResponse;

public class Scenario {

	private String userstory;
	private String originalName;
	private String currentName;
	private String description;
	private String testCase;
	private String owner;
	private String project;
	private String validationInput;
	private String preConditions;
	private boolean mobileTablet = false;
	private boolean mobilePhone = false;
	private boolean spanish = false;
	private String workspace = "https://rally1.rallydev.com/slm/webservice/v2.0/workspace/14457696030";

	private List<String> testCases = new ArrayList<String>();

	public Scenario() {
	};

	public Scenario(String originalName, String description, String testCase, String owner, String project,
			String validationInput, String preConditions, String userstory) {
		this.originalName = originalName;
		this.description = description;
		this.testCase = testCase;
		this.owner = owner;
		this.project = project;
		this.validationInput = validationInput;
		this.preConditions = preConditions;
		this.currentName = this.originalName;
		this.userstory = userstory;
	}

	void UploadTest() {
		JsonObject newTestCase = new JsonObject();
		newTestCase.addProperty("Name", currentName);
		newTestCase.addProperty("Description", description.replace("\n<p", "<p").replace("<br/>\n", "<br />"));
		newTestCase.addProperty("TestCase", testCase);
		newTestCase.addProperty("Owner", owner);
		newTestCase.addProperty("Project", project);
		newTestCase.addProperty("Workspace", workspace);
		newTestCase.addProperty("WorkProduct", testCase);
		newTestCase.addProperty("ValidationInput", validationInput.replace("\n", "<p>"));
		newTestCase.addProperty("PreConditions", preConditions.replace("\n", "<p>"));

		// ------------------------------------------------------

		CreateRequest createRequest = new CreateRequest("testcase", newTestCase);
		CreateResponse createResponse = null;

		try {
			createResponse = RallyMassUploaderGui.restApi.create(createRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (createResponse.wasSuccessful()) {
			RallyMassUploaderGui.systemOutputTextBox.setText(
					RallyMassUploaderGui.systemOutputTextBox.getText() + "\"" + currentName + "\" has been Uploaded\n");
			testCases.add(createResponse.getObject().getAsJsonObject().get("_ref").getAsString());
		} else {
			RallyMassUploaderGui.systemOutputTextBox.setText(RallyMassUploaderGui.systemOutputTextBox.getText()
					+ String.format("\n\t%s", createResponse.getErrors()));
		}
	}

	void DeleteTests() {
		for (String testCase : testCases) {
			DeleteRequest deleteRequest = new DeleteRequest(testCase);
			DeleteResponse deleteResponse = null;

			// ------------------------------------------------------

			try {
				deleteResponse = RallyMassUploaderGui.restApi.delete(deleteRequest);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (deleteResponse.wasSuccessful()) {
				RallyMassUploaderGui.systemOutputTextBox.setText(
						RallyMassUploaderGui.systemOutputTextBox.getText() + "\"" + testCase + "\" has been Removed\n");
			} else {
				RallyMassUploaderGui.systemOutputTextBox.setText(RallyMassUploaderGui.systemOutputTextBox.getText()
						+ String.format("\n\t%s", deleteResponse.getErrors()));
			}
		}
		testCases = new ArrayList<String>();
	}

	public boolean isMobileTablet() {
		return mobileTablet;
	}

	public boolean isMobilePhone() {
		return mobilePhone;
	}

	public void setMobileTablet(boolean mobileTablet) {
		this.mobileTablet = mobileTablet;
	}

	public void setMobilePhone(boolean mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public boolean isSpanish() {
		return spanish;
	}

	public void setSpanish(boolean spanish) {
		this.spanish = spanish;
	}

	public List<String> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<String> testCases) {
		this.testCases = testCases;
	}

	public String getUserstory() {
		return userstory;
	}

	public void setUserstory(String userstory) {
		this.userstory = userstory;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTestCase() {
		return testCase;
	}

	public void setTestCase(String testCase) {
		this.testCase = testCase;
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

	public String getValidationInput() {
		return validationInput;
	}

	public void setValidationInput(String validationInput) {
		this.validationInput = validationInput;
	}

	public String getPreConditions() {
		return preConditions;
	}

	public void setPreConditions(String preConditions) {
		this.preConditions = preConditions;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

}
