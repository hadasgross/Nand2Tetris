package ex8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
	
	private static final String WHITESPACE = "\\s*";
	private static final String COMMENT_REGEX = "(//+.*)?";
	private static final String NAME_REGEX = "([^\\d](\\w|\\_|\\d|\\.|\\:)+)";
	private static final String SEGMENT = "(argument|local|static|constant|"
			+ "this|that|pointer|temp)";
	private static final String C_ARITHMETIC_REGEX = WHITESPACE + "(add|sub|neg|eq|"
			+ "gt|lt|and|or|not)" + WHITESPACE + COMMENT_REGEX;
	private static final String C_PUSH_REGEX = WHITESPACE + "push" + WHITESPACE 
						+ SEGMENT + WHITESPACE + "(\\d+)" + WHITESPACE + COMMENT_REGEX;
	private static final String C_POP_REGEX = WHITESPACE + "pop" + WHITESPACE 
			+ SEGMENT + WHITESPACE + "(\\d+)" + WHITESPACE + COMMENT_REGEX;
	private static final String C_LABEL_REGEX = WHITESPACE + "label" + WHITESPACE + NAME_REGEX, C_GOTO_REGEX =
			WHITESPACE + "goto" + NAME_REGEX + COMMENT_REGEX, C_IF_REGEX = WHITESPACE + "if-goto" + NAME_REGEX +
            COMMENT_REGEX;
	
	private BufferedReader reader;
	private String currentLine;
	
	/**
	 * Opens the input file/stream and gets ready to parse it.
	 * @param inputFile
	 */
	Parser(File inputFile) throws IOException
	{
		this.reader = new BufferedReader(new FileReader(inputFile));
		advance();
	}
	
	/**
	 * Are there more commands in the input? 
	 * @throws IOException 
	 */
	public boolean hasMoreCommands() throws IOException{
		if (currentLine == null)
		{
			reader.close();
			return false;
		}
		return true;
	}
	
	// Handle whitespaces: empty lines, comments, bad lines.
	public boolean checkEmpty() throws IOException
	{
		if (currentLine.isEmpty())
		{
			currentLine = reader.readLine();
			return true;
		}
		return false;
	}
	
	public boolean checkComment() throws IOException
	{
		if (currentLine.matches(WHITESPACE + COMMENT_REGEX))
		{
			currentLine = reader.readLine();
			return true;
		}
		return false;
	}
	/**
	 *  Reads the next command from the input and makes it the current command.
	 *  Should be called only if hasMoreCommands() is true.
		Initially there is no current command. 
	 */
	public void advance() throws IOException
	{
		currentLine = reader.readLine();	
	}
	
	/**
	 * @return Returns the type of the current VM command. 
	 */
	public VMCommand getCommandType()
	{
		Pattern pattern = Pattern.compile(C_ARITHMETIC_REGEX);
		Matcher matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String operation = matcher.group(1);
			return new C_Arithmetic(operation);
		}
		pattern = Pattern.compile(C_PUSH_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String segment = matcher.group(1);
			int value = Integer.parseInt(matcher.group(2));
			return new C_Push(segment, value);
		}
		pattern = Pattern.compile(C_POP_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String segment = matcher.group(1);
			int value = Integer.parseInt(matcher.group(2));
			return new C_Pop(segment, value);
		}
		pattern = Pattern.compile(C_LABEL_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
			String labelName = matcher.group(1);
			return new C_Label(labelName);
		}
		pattern = Pattern.compile(C_GOTO_REGEX);
		matcher = pattern.matcher(currentLine);
		if (matcher.matches())
		{
            return new C_Goto(matcher.group(1));
		}
        pattern = Pattern.compile(C_IF_REGEX);
        matcher = pattern.matcher(currentLine);
        if (matcher.matches()) {
            return new C_If(matcher.group(1));
        }
		return null;
	}
	
	/**
	 * @return Returns the first argument of the current command.
	 */
	public String getArg1()
	{
		return this.getCommandType().getFirstArg();
	}
	/**
	 * @return Returns the second argument of the current command.
	 */
	public int getArg2()
	{
		return this.getCommandType().getSecondArg();
	}

}
