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
package org.xmlbeam.util.intern.duplexd.org.w3c.xqparser;

/**
 * @author sven
 */
public enum ExpressionType {
    NODE(false), ELEMENT(false), ATTRIBUTE(false), VALUE(true);

    private ExpressionType(final boolean mustEvaluateAsString) {
        this.mustEvaluateAsString = mustEvaluateAsString;
    }

    private final boolean mustEvaluateAsString;

    /**
     * Some expressions can not be evaluated as node or node lists. (e.g. functions) These must be
     * evaluated as Strings.
     *
     * @return true for values
     */
    public boolean isMustEvalAsString() {
        return mustEvaluateAsString;
    };
}
