/**
 * JSkat - A skat program written in Java
 * by Jan Schäfer, Markus J. Luzius and Daniel Loreck
 *
 * Version 0.12.0
 * Copyright (C) 2013-05-09
 *
 * Licensed under the Apache License, Version 2.0. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jskat.player;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;

/**
 * Resolves all implementations of {@link JSkatPlayer} that inherit from
 * {@link AbstractJSkatPlayer} and are not abstract
 */
public class JSkatPlayerResolver {

	public final static String HUMAN_PLAYER_CLASS = "org.jskat.gui.human.SwingHumanPlayer";

	public final static Set<String> EXCLUDED_PLAYER_CLASSES;

	static {
		EXCLUDED_PLAYER_CLASSES = new HashSet<String>();
		EXCLUDED_PLAYER_CLASSES.add("org.jskat.ai.mjl.AIPlayerMJL");
		EXCLUDED_PLAYER_CLASSES
				.add("org.jskat.ai.algorithmic.AlgorithmicAIPlayer");
	}

	/**
	 * Gets all class names including package names of AI player implementations
	 */
	public static Set<String> getAllAIPlayerImplementations() {

		Set<String> result = getAllImplementations();

		result.removeAll(EXCLUDED_PLAYER_CLASSES);

		return result;
	}

	private static Set<String> getAllImplementations() {
		Set<String> result = new HashSet<String>();
		Reflections reflections = new Reflections("org.jskat");

		Set<Class<? extends AbstractJSkatPlayer>> subTypes = reflections
				.getSubTypesOf(AbstractJSkatPlayer.class);
		for (Class<? extends AbstractJSkatPlayer> jskatPlayer : subTypes) {
			if (isNotAbstract(jskatPlayer) && isNotHumanPlayer(jskatPlayer)) {
				result.add(jskatPlayer.getName());
			}
		}
		return result;
	}

	private static boolean isNotHumanPlayer(
			Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !HUMAN_PLAYER_CLASS.equals(jskatPlayer.getName());
	}

	private static boolean isNotAbstract(
			Class<? extends AbstractJSkatPlayer> jskatPlayer) {
		return !Modifier.isAbstract(jskatPlayer.getModifiers());
	}
}
