package org.playframework.playclipse.handlers;

import org.playframework.playclipse.Editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;

import java.util.regex.*;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class GoToViewHandler extends AbstractHandler {

	/**
	 * The constructor.
	 */
	public GoToViewHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String line;
		String viewName = null;
		Editor editor = Editor.getCurrent(event);
		int lineNo = editor.getCurrentLineNo();
		line = editor.getLine(lineNo);
		if (line.contains("render")) {
			Pattern pt = Pattern.compile("\"(.*)\"");
			Matcher m = pt.matcher(line);
			if (m.find()) {
				// There is a custom view
				viewName = m.group().replace("\"", ""); // m.group();
			} else {
//				Pattern pt2 = Pattern.compile("[a-zA-Z0-9_]+(");
				// No custom view, let's go up until we get the action name
				while (lineNo > 0 && viewName == null) {
					line = editor.getLine(lineNo--);
					if (line.contains("public") && line.contains("static") && line.contains("void")) {
						Pattern pt2 = Pattern.compile("\\w+\\s*\\(");
						Matcher m2 = pt2.matcher(line);
						if (m2.find()) {
							String action = m2.group().replace("(", "").trim();
							String controllerName = editor.getTitle().replace(".java", "");
							viewName = controllerName + "/" + action + ".html";
						}
					}
				}
			}
		}

		MessageDialog.openInformation(
				window.getShell(),
				"Playclipse",
				viewName);
		return null;
	}
}