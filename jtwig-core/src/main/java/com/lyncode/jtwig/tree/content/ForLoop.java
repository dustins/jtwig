/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lyncode.jtwig.tree.content;

import com.lyncode.jtwig.JtwigContext;
import com.lyncode.jtwig.exception.CalculateException;
import com.lyncode.jtwig.exception.CompileException;
import com.lyncode.jtwig.exception.RenderException;
import com.lyncode.jtwig.functions.util.ObjectIterator;
import com.lyncode.jtwig.parser.JtwigParser;
import com.lyncode.jtwig.parser.positioning.Position;
import com.lyncode.jtwig.tree.api.AbstractContent;
import com.lyncode.jtwig.tree.api.Content;
import com.lyncode.jtwig.tree.api.Expression;
import com.lyncode.jtwig.tree.expressions.Variable;
import com.lyncode.jtwig.tree.helper.RenderStream;
import com.lyncode.jtwig.tree.structural.Block;
import com.lyncode.jtwig.unit.resource.JtwigResource;

public class ForLoop extends AbstractContent {
    protected Variable variable;
    protected JtwigContent content;
    protected Expression expression;

    public ForLoop(Position position, Variable variable, Expression list) {
        super(position);
        this.variable = variable;
        this.expression = list;
    }


    public ForLoop setContent(JtwigContent content) {
        this.content = content;
        return this;
    }


    @Override
    public void render(RenderStream renderStream, JtwigContext context) throws RenderException {
        try {
            ObjectIterator iterator = new ObjectIterator(expression.calculate(context));
            Loop loop = new Loop(iterator.size());
            context.set("loop", loop);
            int index = 0;
            while (iterator.hasNext()) {
                loop.update(index++);
                Object object = iterator.next();
                context.set(variable.getIdentifier(), object);
                content.render(renderStream, context);
            }
        } catch (CalculateException e) {
            throw new RenderException(e);
        }
    }

    @Override
    public Content compile(JtwigParser parser, JtwigResource resource) throws CompileException {
        content = content.compile(parser, resource, begin(), end());
        return this;
    }

    @Override
    public boolean replace(Block expression) throws CompileException {
        return content.replace(expression);
    }

    public String toString() {
        return "For each element of " + expression + " render " + content;
    }

    public static class Loop {
        private int index = 0;
        private int length;

        public Loop(int length) {
            this.length = length;
        }

        public void update(int index) {
            this.index = index;
        }

        public int getLength() {
            return length;
        }

        public int getIndex() {
            return index;
        }

        public int getRevindex() {
            return length - index - 1;
        }

        public boolean isFirst() {
            return index == 0;
        }

        public boolean isLast() {
            return index == length - 1;
        }
    }
}
