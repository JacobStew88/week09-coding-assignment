package projects;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exceptions.DbException;
import projects.service.ProjectsService;

public class Projects {
	private Scanner scanner = new Scanner(System.in);
	private ProjectsService projectsService = new ProjectsService();
	private Project curProject ;

	// @formatter:off
	private List<String> operations = List.of(
			"1) Add project to table",
			"2) List projects",
			"3) Select a project"
			);
	// @formatter:on

	public static void main(String[] args) {
		new Projects().displayMenu();
	}

	private void displayMenu() {
		boolean done = false;

		while (!done) {
			try {
				int operation = getOperation();
				switch (operation) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;
					
				case 2:
					listProjects();
					break;
					
				case 3:
					selectProject();
					break;
					
					default:
						System.out.println("\n" + operation + " is not valid. Try again");
						break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e.toString() + " Try again.");
			}
		}
	}


	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput ("Enter a project ID to select a project");
		
		curProject = null;
		curProject = projectsService.fetchProjectById(projectId);
	}

	private void listProjects() {
		List<Project> projects = projectsService.fetchProjects();
		System.out.println("/nProjects:");
		projects.forEach(project -> System.out.println
				(" " + project.getProjectId() + " : " + project.getProjectName()));
	}

	private void createProject() throws SQLException {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput ("Enter the project difficult (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectsService.addProject(project);
		System.out.println("You have successfully created a project: " + dbProject);
		
	}

	private boolean exitMenu() {
		System.out.println("\nExiting the menu. Goodbye!");
		return true;
	}

	private int getOperation() {
		printOperations();
		Integer op = getIntInput("\nThese are the available selections. Press the Enter key to quit");

		return Objects.isNull(op) ? -1 : op;
	}

	private void printOperations() {
		System.out.println();
		System.out.println("Here are your choices:");

		operations.forEach(op -> System.out.println("	" + op));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	}
}
