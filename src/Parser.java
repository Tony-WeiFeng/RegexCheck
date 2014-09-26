import java.util.regex.*;


public class Parser
{
    public Parser()
    {

    }

    public static StringBuffer getField( String sRecord, char cSeparator, int iFieldNumber )  throws Exception
	{
		int iIndex = 0;
		int iFieldNumberCounter = 1;
		char cLetter;
		StringBuffer sField = new StringBuffer("");

        try
        {
            while( iIndex < sRecord.length() )
		    {
    			cLetter = sRecord.charAt(iIndex);

    			if (cLetter != cSeparator )
    			{
    				sField.append(cLetter);
    			}
    			else
    			{
    				if ( iFieldNumberCounter == iFieldNumber )
    				{
    					return sField;
    				}
    				else
    				{
    					iFieldNumberCounter++;
    					sField = new StringBuffer("");
    				}
    			}

    			iIndex++;
    		}
        }
        catch(Exception ex)
        {
            throw new Exception("Unable to getField from String. Error was: " + ex.getMessage());
        }

        if ( iFieldNumberCounter == iFieldNumber )
    			return sField;
    		else
    			return new StringBuffer("");
	}


    public static String getRestOfString(String sRecord, char cSeparator, int iFieldNumber ) throws Exception
    {
        int iIndex = 0;
        int iFieldNumberCounter = 1;
        char cLetter;

        try
        {
            if (iFieldNumber == 1)
                return sRecord.trim();

            while( iIndex < sRecord.length() )
            {
                cLetter = sRecord.charAt(iIndex);

                if (cLetter == cSeparator )
                {
                    iFieldNumberCounter++;
                    if ( iFieldNumberCounter == iFieldNumber )
                    {
                        return sRecord.substring(iIndex).trim();
                    }
                    else
                    {
                        iIndex++;
                        while( iIndex < sRecord.length() )
                        {
                            cLetter = sRecord.charAt(iIndex);
                            if (cLetter != cSeparator )
                            {
                                if ( iFieldNumberCounter == iFieldNumber )
                                {
                                    return sRecord.substring(iIndex).trim();
                                }
                                break;
                            }
                            iIndex++;
                        }
                    }
                }

                iIndex++;
            }
        }
        catch(Exception ex)
        {
            throw new Exception("Unable to get rest of String. Error was: " + ex.getMessage());
        }

        if ( iFieldNumberCounter == iFieldNumber )
        {
            return sRecord.substring(iIndex).trim();
        }
        else
            return new String("");
    }

   public static StackEntry parseSymbol(String Symbol, int Index) throws Exception
    {
        String SourcePart = null;
        String CodePart = null;
        String module = null;
        String object = null;
        String function = null;
        String offset = null;
        StackEntry stackEntry = null;
        boolean HasSource = false;

        try
        {
            /*Pattern p1 = Pattern.compile(".{8}\s.{8}\s(.*)(?:\s\[(.*)\])?", Pattern.CASE_INSENSITIVE + Pattern.COMMENTS + );
            Matcher m = p.matcher(SymbolRecord);

            Regex.Match(line, @".{8}\s.{8}\s(.*)(?:\s\[(.*)\])?",
						RegexOptions.RightToLeft | RegexOptions.IgnorePatternWhitespace | RegexOptions.IgnoreCase);     */

            if (Symbol.trim().endsWith("]"))
            {
                int ParseCallStack = Symbol.trim().lastIndexOf("[", Symbol.trim().lastIndexOf("]"));
                if (ParseCallStack != -1)
                {
                    SourcePart = new String(Symbol.trim().substring(ParseCallStack,Symbol.trim().lastIndexOf("]") + 1));
                    CodePart = new String(Symbol.trim().substring(0, ParseCallStack));
                    HasSource = true;
                }
                else
                {
                    CodePart = new String(Symbol.trim());
                }
            }
            else
            {
                CodePart = new String(Symbol.trim());
            }

            String SymbolRecord = new String(getRestOfString(CodePart, ' ', 4));

            /* Austin Dai - 7/14/2008 - fixing no regex match in the case of '_Node::_Node' as function name,
             * that is to access member variable directly. 
             * (?:                      								# match operator override
            		operator\s*[,!=%&\(\)\*\+\-<>\/\[\]\^\|~]{1,3}\s*  	# detect operator overload
            		|[^:<\+]+		 									# function name, ':' will be removed
				)
            */
            
            // Austin Dai - 4/23/2008 - Use "@+" as a offset indicator to avoid conflict with '+' in module name.
            // Special case: AutoCamMax!__onexitbegin <PERF> (AutoCamMax+0x7d440)
            int _indexOfPlus = SymbolRecord.lastIndexOf("+0x");
            if (_indexOfPlus != -1 && SymbolRecord.lastIndexOf("PERF") == -1)
            	SymbolRecord = SymbolRecord.substring(0, _indexOfPlus) + '@' + SymbolRecord.substring(_indexOfPlus);
            
            Pattern p = Pattern.compile("^([\\w\\s<>`\\+]+)(?:\\!(?:((?:[\\w\\s'`:]+(?:<.+>|))|(?:<lambda_[\\da-f]+>))::|)((?:operator\\s*[,!=%&\\(\\)\\*\\+\\-<>\\/\\[\\]\\^\\|~]{1,3}\\s*|[^<\\+\\(\\)]+)(?:<.+>|)(?:\\s*<PERF>\\s+\\([\\w\\s\\+]+\\)|))|)(?:@\\+(0x[\\da-f]+)|)$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(SymbolRecord);

            boolean matchFound = m.find();
			boolean isManaged = false;
			
			if (!matchFound)
			{
				// match managed function call
				p = Pattern.compile("^(?:([\\w<>]+)(?:\\!)|)(?:(?:([\\w`\\.\\+<>]+(?:\\[.+\\]|))(?:\\.))((?:[^:\\+]+)(?:\\[.+\\]|)))(?:\\((.*)\\))(?:@\\+(0x[\\da-f]+)|)\\s*(?:\\[.+\\]|)$", Pattern.CASE_INSENSITIVE);
				m = p.matcher(SymbolRecord);
				matchFound = m.find();
				if (matchFound)
				{
					isManaged = true;
				}
			}

            if (matchFound)
            {
                if ((m.group(1) == null) || (m.group(1).length() < 1) || (m.group(1).indexOf("0x") == 0))
                {
                    // module = "_NA_";
                    module = "<no matching symbol>";
                }
                else
                {
                    if (m.group(1).trim().length() > 64)
                    {
                        module = m.group(1).trim().substring(0, 61) + "...";
                    }
                    else
                    {
                        module = m.group(1).trim();
                    }
                }

                if ((m.group(2) == null) || (m.group(2).length() < 1 ))
                {
                    // object = "_NA_";
                    object = null;
                }
                else
                {
                    if (m.group(2).trim().length() > 256)
                    {
                        object = m.group(2).trim().substring(0, 253) + "...";
                    }
                    else
                    {
                        object = m.group(2).trim();
                    }
                }

                if ((m.group(3) == null) || (m.group(3).length() < 1 ))
                {
                    // function = "_NA_";
                    function = null;
                }
                else
                {
                    if (m.group(3).trim().length() > 256)
                    {
                        function = m.group(3).trim().substring(0, 253) + "...";
                    }
                    else
                    {
                        function = m.group(3).trim();
                    }
                }

				if (isManaged)
				{
					if ((m.group(5) == null) || (m.group(5).length() < 3 ))
					{
						offset = "no matching symbol";
					}
					else
					{
						// why here use module instead of m.group(1)?
                    	if (m.group(5).trim().length() > 32)
                    	{
                       		offset = "0x" + m.group(5).trim().substring(2, 29).toUpperCase() + "...";
                    	}
                    	else
                    	{
							offset = "0x" + m.group(5).trim().substring(2).toUpperCase();
                    	}
                	}
				}
				else
				{
	                if ((m.group(4) == null) || (m.group(4).length() < 3 ))
	                {
	                    //offset = "_NA_";
	                    offset = "no matching symbol";
	                    // offset = null;
	                }
	                else
	                {
	                    if (m.group(4).trim().length() > 32)
	                    {
	                        offset = "0x" + m.group(4).trim().substring(2, 29).toUpperCase() + "...";
	                    }
	                    else
	                    {
	                        offset = "0x" + m.group(4).trim().substring(2).toUpperCase();
	                    }
	                }
				}
            }
            else
            {
                module = "CER system was unable to parse this stack line. No RegEx match.";
                object = "no RegEx match";
                function = "no RegEx match";
                offset = "no RegEx match";
            }

            if (module.equalsIgnoreCase("<no matching symbol>"))
            {
                stackEntry = new StackEntry("no matching symbol", object, function, "no matching symbol", Index, 1);
            }
            else
            {
                stackEntry = new StackEntry(module, object, function, offset, Index, 0);
            }

            if (HasSource)
            {
                stackEntry.setSourceFile(SourcePart);
            }
			if (isManaged)
			{
				stackEntry.setIsManaged(1);
			}
			else
			{
				stackEntry.setIsManaged(0);
			}
        }
        catch (Exception e)
        {
            throw new Exception("Unable to parse Symbol: " + Symbol + " Error was: " + e.getMessage());
        }

        return stackEntry;
    }
}

