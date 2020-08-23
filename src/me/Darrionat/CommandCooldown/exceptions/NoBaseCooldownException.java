package me.Darrionat.CommandCooldown.exceptions;

public class NoBaseCooldownException extends Exception {

	private static final long serialVersionUID = -2929420720152624573L;
	public static final String ERROR_STRING = "No '*' Cooldown Defined In Configuration Section 'sectionName'";

	public NoBaseCooldownException(String message) {
		super(message);
	}
}
