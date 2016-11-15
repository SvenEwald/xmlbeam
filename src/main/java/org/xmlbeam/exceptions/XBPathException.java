/**
 *  Copyright 2014 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.xmlbeam.exceptions;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Exception to provide error details related to XPath parsing.
 * 
 * @author Sven
 */
public class XBPathException extends XBException {

    private static final long serialVersionUID = -2286603725835988440L;
    private final String resolvedXpath;

    /**
     * Constructor.
     * @param msg
     * @param method
     * @param xpath
     */
    public XBPathException(final String msg,final Method method, final String xpath){
        super(msg+" when invoking "+shortDesc(method)+" [Resolved XPath:'"+xpath+"']");
        this.resolvedXpath = xpath;
        stripStackTrace();
    }
    
    /**
     * Constructor.
     * @param cause
     * @param method
     * @param xpath
     */
    public XBPathException(final Throwable cause, final Method method, final String xpath) {        
        super("Exception invocating "+shortDesc(method)+" [Resolved XPath:'"+xpath+"']",cause);
        this.resolvedXpath = xpath;
        stripStackTrace();
    }

    private void stripStackTrace() {
        List<StackTraceElement> st = new LinkedList<StackTraceElement>(Arrays.asList(getStackTrace()));
        st.remove(0);
        st.remove(0);
        setStackTrace(st.toArray(new StackTraceElement[st.size()]));
    }

    /**
     * @param method
     * @return
     */
    private static String shortDesc(Method method) {
        String params="";
        for (Class<?> c:method.getParameterTypes()){
            if (!params.isEmpty()) {
                params=", ";
            }
            params+=c.getSimpleName();
        }
        return method.getDeclaringClass().getSimpleName()+"."+method.getName()+"("+params+")";
    }

    /**
     * @return the xpath with all parameters filled in.
     */
    public String getResolvedXpath() {
        return resolvedXpath;
    }
}
