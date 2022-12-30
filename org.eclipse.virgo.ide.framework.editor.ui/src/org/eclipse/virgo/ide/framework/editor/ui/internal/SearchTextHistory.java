/*******************************************************************************
 * Copyright (c) 2009, 2011 SpringSource, a divison of VMware, Inc. and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     SpringSource, a division of VMware, Inc. - initial API and implementation
 *     SAP AG - enhancements
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.virgo.ide.framework.editor.ui.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Dupuis
 * @author Kaloyan Raev
 */
public class SearchTextHistory {

	private static final int MAX_HISTORY = 10;

	private List<String> history= new ArrayList<String>();

	private int index = 0;

	public void add(String text) {
		int found = -1;
        int size = history.size();
        for (int i = 0; i < size; i++) {
            String s = history.get(i);
            if (s.equals(text)) {
                found = i;
                break;
            }
        }

        if (found == -1) {
            if (size >= MAX_HISTORY)
                history.remove(size - 1);
            history.add(0, text);
        } else if (found != 0) {
            history.remove(found);
            history.add(0, text);
        }
        index = 0;
	}

	public String back() {
		if (canBack()) {
			String text = history.get(index);
			index++;
			return text;
		}
		return null;
	}

	public String forward() {
		if (canForward()) {
			index--;
			String text = history.get(index);
			return text;
		}
		return null;
	}

	public boolean canForward() {
		return index > 0;
	}

	public boolean canBack() {
		return index < history.size();
	}

	public String current() {
		return (history.size() == 0) ? null : history.get(0);
	}
}
