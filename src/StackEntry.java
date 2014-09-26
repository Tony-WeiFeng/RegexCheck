import java.io.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Autodesk</p>
 *
 * @author not attributable
 * @version 1.0
 */

public class StackEntry implements Serializable
{
    // Fields
    protected String module;
    protected String object;
    protected String function;
    protected String offset;
    protected int stackIndex;
    protected int noMatchingSymbol;
    protected String sourceFile;
	protected int isManaged;
    public static final String NOT_APPLICABLE = "_NA_";

    // Constructors
    public StackEntry(String moduleEntry, String objectEntry, String functionEntry, String offsetEntry, int stackIndexEntry, int noMatchingSymbolEntry)
    {
        module = moduleEntry;
        object = objectEntry;
        function = functionEntry;
        offset = offsetEntry;
        stackIndex = stackIndexEntry;
        noMatchingSymbol = noMatchingSymbolEntry;
		isManaged = 0;
    }

    public StackEntry()
    {

    }

    // Methods

    /**
     * Normalize
     * <p/>
     * Applies business logic to how stack entries should be stored.  At this point
     * there are three business rules:
     * <p/>
     * 1) All modules should be stored in lower case
     * 2) Any module with a "kernel32" value with null object and function values
     * should have it's offset values set to "_NA_"
     * 3) Any null object and function values should be replaced with "_NA_"
     */
    public void normalize()
    {

        // Setting the module to lower case
        module = this.module.toLowerCase();

        // Checking to see if the offset should be reset
        if ( (module.equals("kernel32")) && (object == null) && (function == null) )
        {
            offset = NOT_APPLICABLE;
        }

        if (object == null)
        {
            object = NOT_APPLICABLE;
        }

        if (function == null)
        {
            function = NOT_APPLICABLE;
        }
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction(String functionEntry)
    {
        function = functionEntry;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String moduleEntry)
    {
        module = moduleEntry;
    }

    public String getObject()
    {
        return object;
    }

    public void setObject(String objectEntry)
    {
        object = objectEntry;
    }

    public String getOffset()
    {
        return offset;
    }

    public void setOffset(String offsetEntry)
    {
        offset = offsetEntry;
    }

    public int getStackIndex()
    {
        return stackIndex;
    }

    public void setStackIndex(int stackIndexEntry)
    {
        stackIndex = stackIndexEntry;
    }

    public int getNoMatchingSymbol()
    {
        return noMatchingSymbol;
    }

    public void setNoMatchingSymbol(int noMatchingSymbolEntry)
    {
        noMatchingSymbol = noMatchingSymbolEntry;
    }

    public String getSourceFile()
    {
        return sourceFile;
    }

    public void setSourceFile(String sourceFileEntry)
    {
        sourceFile = sourceFileEntry;
    }

	public int getIsManaged()
	{
		return isManaged;
	}

	public void setIsManaged(int isManagedEntry)
	{
		isManaged = isManagedEntry;
	}
    /**
     * Overrides Object.toString() to return more descriptive instance information.
     *
     * @return
     */
    public String toString()
    {

        StringBuffer result = new StringBuffer(super.toString());

        result.append(": stackIndex=").append(stackIndex).append(", module=").append(module).append(", object=").append(object).append(", function=").append(function).append(", function=").append(function).append(", offset=").append(offset).append(", noMatchingSymbol=").append(noMatchingSymbol).append(", sourceFile=").append(sourceFile).append(", isManaged=").append(isManaged);

        return result.toString();
    }

}
