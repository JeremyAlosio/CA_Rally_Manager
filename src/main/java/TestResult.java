import java.io.Serializable;

public class TestResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String Name;
	private String TestResult;
	private String WhichToPass;
	
	public TestResult(String _Name, String _TestResult, String _WhichToPass)
	{
		this.setName(_Name);
		this.setTestResult(_TestResult);
		this.setWhichToPass(_WhichToPass);	
	}

	public String getTestResult() {
		return TestResult;
	}

	public void setTestResult(String testResult) {
		TestResult = testResult;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getWhichToPass() {
		return WhichToPass;
	}

	public void setWhichToPass(String whichToPass) {
		this.WhichToPass = whichToPass;
	}
	
	
	
}
