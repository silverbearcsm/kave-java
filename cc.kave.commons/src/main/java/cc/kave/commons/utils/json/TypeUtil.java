/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
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
package cc.kave.commons.utils.json;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtil {

	private static Pattern regex1 = Pattern.compile("cc\\.kave\\.commons\\.model\\.ssts\\.impl\\.([.a-zA-Z0-9_]+)");
	private static Pattern regex2 = Pattern.compile("cc\\.kave\\.commons\\.model\\.([.a-zA-Z0-9_]+)");
	private static Pattern regex3 = Pattern.compile("\"KaVE.Model.([.a-zA-Z0-9_]+), KaVE.Model\"");
	private static Pattern regex4 = Pattern
			.compile("cc\\.kave\\.commons\\.model\\.events\\.completionevent\\.([.a-zA-Z0-9_]+)");
	private static Pattern regex5 = Pattern.compile("\"KaVE([.a-zA-Z0-9_]+), KaVE.Commons\"");
	private static Pattern regex6 = Pattern.compile("\\[SST:([.a-zA-Z0-9_]+)\\]");

	public static String toCSharpTypeNames(String json) {
		// TODO: ugly hack to handle type conversion that is both slow and hard
		// to maintain... improve solution!
		String sstReplaced = replacePattern(json, "cc\\.kave\\.commons\\.model\\.ssts\\.impl\\.([.a-zA-Z0-9_]+)",
				"[SST:", "]", false);
		String vsReplaced = replacePattern(sstReplaced,
				"cc\\.kave\\.commons\\.model\\.events\\.visualstudio\\.([.a-zA-Z0-9_]+)",
				"KaVE.Commons.Model.Events.VisualStudio.", ", KaVE.Commons", false);
		String testsReplaced = replacePattern(vsReplaced,
				"cc\\.kave\\.commons\\.model\\.events\\.testrunevents\\.([.a-zA-Z0-9_]+)",
				"KaVE.Commons.Model.Events.TestRunEvents.", ", KaVE.Commons", false);
		String upeReplaced = replacePattern(testsReplaced,
				"cc\\.kave\\.commons\\.model\\.events\\.userprofiles\\.([.a-zA-Z0-9_]+)",
				"KaVE.Commons.Model.Events.UserProfiles.", ", KaVE.Commons", false);
		String vcsReplaced = replacePattern(upeReplaced,
				"cc\\.kave\\.commons\\.model\\.events\\.versioncontrolevents\\.([.a-zA-Z0-9_]+)",
				"KaVE.Commons.Model.Events.VersionControlEvents.", ", KaVE.Commons", false);
		String modelClassesReplaced = replacePattern(vcsReplaced, "cc\\.kave\\.commons\\.model\\.([.a-zA-Z0-9_]+)",
				"KaVE.Commons.Model.", ", KaVE.Commons", false);
		return modelClassesReplaced;
	}

	public static String toJavaTypeNames(String json) {
		String legacySupport_PackageNames = legacySupport_PackageNames(json);
		String javaPackages = toJavaPackages(legacySupport_PackageNames);
		String completionEvent_formatting = completionEvent_formatting(javaPackages);
		return legacySupport_CompletionEvents(completionEvent_formatting);

	}

	private static String legacySupport_PackageNames(String json) {
		return replacePattern(json, regex3, "\"KaVE.Commons.Model.", ", KaVE.Commons\"", false);
	}

	private static String legacySupport_CompletionEvents(String json) {
		return replacePattern(json, regex4, "cc\\.kave\\.commons\\.model\\.events\\.completionevents\\.", "", true);
	}

	private static String completionEvent_formatting(String json) {
		return replacePattern(json, regex5, "\"cc.kave", "\"", true);
	}

	private static String toJavaPackages(String json) {
		return replacePattern(json, regex6, "cc.kave.commons.model.ssts.impl.", "", true);
	}

	private static String replacePattern(String type, String pattern, String prefix, String suffix, boolean lower) {
		Pattern regex = Pattern.compile(pattern);
		return replacePattern(type, regex, prefix, suffix, lower);
	}

	private static String replacePattern(String type, Pattern regex, String prefix, String suffix, boolean lower) {
		StringBuffer resultString = new StringBuffer();
		Matcher regexMatcher = regex.matcher(type);
		while (regexMatcher.find()) {
			String replacement = prefix + (lower ? allToLowerCaseNamespace(regexMatcher.group(1))
					: toUpperCaseNamespace(regexMatcher.group(1))) + suffix;
			regexMatcher.appendReplacement(resultString, replacement);
		}
		regexMatcher.appendTail(resultString);
		return resultString.toString();
	}

	private static String allToLowerCaseNamespace(String group) {
		if (group.lastIndexOf('.') != -1) {
			return group.substring(0, group.lastIndexOf('.')).toLowerCase() + group.substring(group.lastIndexOf('.'));
		}
		return group;
	}

	private static String toUpperCaseNamespace(String string) {
		String[] path = string.split("[.]");
		String type = "";
		for (int i = 0; i < path.length; i++) {
			if (i != path.length - 1) {
				if (path[i].equals("loopheader"))
					path[i] = "loopHeader";
				else if (path[i].equals("completionevents"))
					path[i] = "completionEvents";
				else if (path[i].equals("typeshapes"))
					path[i] = "typeShapes";
				type += path[i].substring(0, 1).toUpperCase() + path[i].substring(1) + ".";
			} else
				type += path[i];
		}
		return type;
	}
}
