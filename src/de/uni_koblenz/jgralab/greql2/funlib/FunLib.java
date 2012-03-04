/*
 * JGraLab - The Java Graph Laboratory
 *
 * Copyright (C) 2006-2012 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 *
 * For bug reports, documentation and further information, visit
 *
 *                         https://github.com/jgralab/jgralab
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */
package de.uni_koblenz.jgralab.greql2.funlib;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Logger;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.funlib.Function.Category;
import de.uni_koblenz.jgralab.greql2.types.Types;
import de.uni_koblenz.jgralab.greql2.types.Undefined;

public class FunLib {
	private static final Map<String, FunctionInfo> functions;
	private static final Logger logger;

	static {
		logger = JGraLab.getLogger(FunLib.class.getPackage().getName());
		functions = new HashMap<String, FunctionInfo>();
		// register builtin functions
		if (logger != null) {
			logger.fine("Registering builtin functions");
		}
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Abs.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Add.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Ceil.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Cos.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Div.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Exp.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Floor.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Ln.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Mod.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Mul.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Neg.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Round.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Sin.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Sqrt.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Sub.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.Tan.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.ToDouble.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.ToInteger.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.artithmetics.ToLong.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitAnd.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitNot.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitOr.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitShl.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitShr.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitUnsignedShr.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.bitops.BitXor.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Contains.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.ContainsKey.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.ContainsValue.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Difference.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.EntrySet.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Get.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Intersection.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.IsEmpty.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.IsSubSet.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.KeySet.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Pos.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Sort.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.SortByColumn.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.SubCollection.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.TheElement.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.ToList.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.ToSet.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Union.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.collections.Values.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Alpha.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Degree.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.DegreeFunction.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Depth.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Describe.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Distance.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Edges.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgesConnected.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgesFrom.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgesTo.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgeTrace.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgeTypeSubgraph.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EdgeSetSubgraph.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.ElementSetSubgraph.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.EndVertex.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.GetEdge.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.GetValue.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.GetVertex.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Id.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.InDegree.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.IsAcyclic.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.IsIsolated.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.IsLoop.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.IsReachable.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Leaves.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Next.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Omega.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.OutDegree.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.PathLength.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.PathSystem.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.ReachableVertices.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Slice.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.StartVertex.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.This.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.That.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.TopologicalSort.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.VertexTrace.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.Vertices.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.VertexTypeSubgraph.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.graph.VertexSetSubgraph.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.logics.And.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.logics.Not.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.logics.Or.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.logics.Xor.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.misc.IsDefined.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.misc.IsUndefined.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.misc.Log.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.misc.ValueType.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.Equals.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.GrEqual.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.GrThan.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.LeEqual.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.LeThan.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.relations.Nequals.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.AttributeNames.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.Attributes.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.HasAttribute.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.HasComponent.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.HasType.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.Type.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.schema.TypeName.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Count.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Max.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Mean.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Min.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Sdev.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Sum.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.statistics.Variance.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.CapitalizeFirst.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.Concat.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.EndsWith.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.Join.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.Length.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.ReMatch.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.Split.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.StartsWith.class);
		register(de.uni_koblenz.jgralab.greql2.funlib.strings.ToString.class);
	}

	private FunLib() {
	}

	private static class Signature {
		Class<?>[] parameterTypes;
		Method evaluateMethod;

		final boolean matches(Object[] params) {
			if (params.length != parameterTypes.length) {
				return false;
			}
			for (int i = 0; i < params.length; i++) {
				if (!parameterTypes[i].isInstance(params[i])) {
					return false;
				}
			}
			return true;
		}
	}

	public static class FunctionInfo {
		String name;
		Class<? extends Function> functionClass;
		Function function;
		Signature[] signatures;
		boolean needsGraphArgument;
		boolean acceptsUndefinedValues;

		FunctionInfo(String name, Class<? extends Function> cls) {
			this.name = name;
			functionClass = cls;
			ArrayList<Signature> functionSignatures = new ArrayList<Signature>();
			try {
				function = cls.newInstance();
			} catch (InstantiationException e) {
				throw new GreqlException("Could not instantiate '"
						+ cls.getName() + "'", e);
			} catch (IllegalAccessException e) {
				throw new GreqlException(
						"Could not instantiate '"
								+ cls.getName()
								+ "' (class must be public and needs public default constructor)",
						e);
			}
			needsGraphArgument = cls
					.isAnnotationPresent(NeedsGraphArgument.class);
			acceptsUndefinedValues = cls
					.isAnnotationPresent(AcceptsUndefinedArguments.class);
			registerSignatures(functionSignatures, cls);
			signatures = new Signature[functionSignatures.size()];
			functionSignatures.toArray(signatures);
		}

		void registerSignatures(ArrayList<Signature> signatures,
				Class<? extends Function> cls) {
			for (Method m : cls.getMethods()) {
				if (Modifier.isPublic(m.getModifiers())
						&& !Modifier.isAbstract(m.getModifiers())
						&& m.getName().equals("evaluate")) {
					if (logger != null) {
						logger.finest("\t" + m);
					}
					Signature sig = new Signature();
					sig.evaluateMethod = m;
					sig.parameterTypes = m.getParameterTypes();
					signatures.add(sig);
				}
			}
		}

		public final Function getFunction() {
			return function;
		}

		public final boolean needsGraphArgument() {
			return needsGraphArgument;
		}

		public final boolean acceptsUndefinedValues() {
			return acceptsUndefinedValues;
		}
	}

	public static final boolean contains(String name) {
		return functions.containsKey(name);
	}

	private static final String getFunctionName(String className) {
		return Character.toLowerCase(className.charAt(0))
				+ className.substring(1);
	}

	private static final String getFunctionName(Class<? extends Function> cls) {
		return getFunctionName(cls.getSimpleName());
	}

	public static final String getArgumentAsString(Object arg) {
		if (arg == null) {
			arg = Undefined.UNDEFINED;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(Types.getGreqlTypeName(arg));
		if (arg instanceof String) {
			sb.append(": ").append('"')
					.append(arg.toString().replace("\"", "\\\"")).append('"');
		} else if (!(arg instanceof Graph) && !(arg instanceof Undefined)) {
			sb.append(": ").append(arg);
		}
		return sb.toString();
	}

	public static final Object apply(PrintStream os, String name,
			Object... args) {
		assert name != null && name.length() >= 1;
		assert args != null;
		assert validArgumentTypes(args);
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (args.length == 0) {
			sb.append("()");
		} else {
			String delim = "(";
			for (Object arg : args) {
				sb.append(delim).append(getArgumentAsString(arg));
				delim = ", ";
			}
			sb.append(")");
		}
		os.print(sb);
		Object result = apply(name, args);
		os.println(" -> " + getArgumentAsString(result));
		return result;
	}

	public static final Object apply(FunctionInfo fi, Object... args) {
		assert fi != null;
		if (!fi.acceptsUndefinedValues) {
			for (Object arg : args) {
				if (arg == null || arg == Undefined.UNDEFINED) {
					return Undefined.UNDEFINED;
				}
			}
		}
		for (Signature sig : fi.signatures) {
			if (sig.matches(args)) {
				try {
					Object result = sig.evaluateMethod
							.invoke(fi.function, args);
					assert Types.isValidGreqlValue(result);
					return result == null ? Undefined.UNDEFINED : result;
				} catch (IllegalArgumentException e) {
					throw new GreqlException(e.getMessage(), e.getCause());
				} catch (IllegalAccessException e) {
					throw new GreqlException(e.getMessage(), e.getCause());
				} catch (InvocationTargetException e) {
					throw new GreqlException(e.getMessage(), e.getCause());
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Function '").append(fi.name)
				.append("' not defined for argument types (");
		String delim = "";
		for (Object arg : args) {
			sb.append(delim).append(Types.getGreqlTypeName(arg));
			delim = ", ";
		}
		sb.append(")");
		throw new GreqlException(sb.toString());
	}

	public static final Object apply(String name, Object... args) {
		assert name != null && name.length() >= 1;
		assert args != null;
		assert validArgumentTypes(args);
		FunctionInfo fi = getFunctionInfo(name);
		if (fi == null) {
			throw new GreqlException("Call to unknown function '" + name + "'");
		}
		return apply(fi, args);
	}

	private static final boolean validArgumentTypes(Object[] args) {
		for (Object arg : args) {
			if (!Types.isValidGreqlValue(arg)) {
				throw new GreqlException("Type unknown to GReQL: "
						+ arg.getClass().getName() + ", value: " + arg);
			}
		}
		return true;
	}

	public static final void register(String className)
			throws ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		register(cls.asSubclass(Function.class));
	}

	public static final void register(Class<? extends Function> cls) {
		int mods = cls.getModifiers();
		if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)
				|| !Modifier.isPublic(mods)) {
			return;
		}
		String name = getFunctionName(cls);
		FunctionInfo fn = functions.get(name);
		if (fn != null) {
			if (fn.functionClass == cls) {
				// ok, same class is already registered
				return;
			}
			throw new GreqlException("Duplicate function name '" + name + "'");
		}
		if (logger != null) {
			logger.fine("Registering " + cls.getName() + " as '" + name + "'");
		}
		functions.put(name, new FunctionInfo(name, cls));
	}

	public static final FunctionInfo getFunctionInfo(String functionName) {
		return functions.get(functionName);
	}

	public static final Logger getLogger() {
		return logger;
	}

	public static void generateLaTeXFunctionDocs(String fileName)
			throws IOException {
		LaTeXFunctionDocsGenerator docGen = new LaTeXFunctionDocsGenerator(
				fileName, functions);
		docGen.generate();
	}

	
	
	private static class LaTeXFunctionDocsGenerator {
		private BufferedWriter bw;
		//private final Map<Category, SortedMap<String, FunctionInfo>> cat2funs = new HashMap<Function.Category, SortedMap<String, FunctionInfo>>();
		private final Map<Category, List<AnnotationInfo>> cat2funs = new HashMap<Function.Category, List<AnnotationInfo>>();
		
		LaTeXFunctionDocsGenerator(String fileName,
				final Map<String, FunctionInfo> funs) throws IOException {
			bw = new BufferedWriter(new FileWriter(fileName));
			fillCat2Funs(funs);
		}

		private class AnnotationInfo{
			String name;
			String description;
			String[] params;
			Signature[] signatures;
		}
		
		private void fillCat2Funs(final Map<String, FunctionInfo> funs) {
			for (Entry<String, FunctionInfo> e : funs.entrySet()) {
				Class<?> funClass = e.getValue().getFunction().getClass();
				assert(funClass.getConstructors().length == 1);
				Constructor<?> cons = funClass.getConstructors()[0];
				if(cons.getAnnotation(Description.class)!=null){
					Description des = cons.getAnnotation(Description.class);
					createAnnotationInfoFunction(e, des);
				}else{
					for(Signature sig : e.getValue().signatures){
						Method m = sig.evaluateMethod;
						Description des = m.getAnnotation(Description.class);
						createAnnotationInfoMethod(e, sig, des);
					}
				}			
			}
		}

		private void createAnnotationInfoMethod(Entry<String, FunctionInfo> e,
				Signature sig, Description des) {
			AnnotationInfo aninf = new AnnotationInfo();
			aninf.name = e.getKey();
			aninf.description = des.description();
			aninf.params = des.params();
			aninf.signatures = new Signature []{sig};
			for(Category cat : des.categories()){
				List<AnnotationInfo> m1 = cat2funs.get(cat);
				if(m1==null){
					m1 = new Vector<AnnotationInfo>();
					cat2funs.put(cat,m1);
				}
				m1.add(aninf);
				cat2funs.put(cat, m1);
			}
		}

		private void createAnnotationInfoFunction(
				Entry<String, FunctionInfo> e, Description des) {
			AnnotationInfo aninf = new AnnotationInfo();
			aninf.name = e.getKey();
			aninf.description = des.description();
			aninf.params = des.params();
			aninf.signatures = e.getValue().signatures;
			for(Category cat : des.categories()){
				List<AnnotationInfo> m = cat2funs.get(cat);
				if(m==null){
					m = new Vector<AnnotationInfo>();
					cat2funs.put(cat,m);
				}
				m.add(aninf);
				cat2funs.put(cat, m);
			}
		}


		/**
		 * Set to true to generate a complete latex doc that can be compiled
		 * standalone. Useful when changing this generator...
		 */
		private boolean STANDALONE = true;

		void generate() throws IOException {
			try {
				if (STANDALONE) {
					write("\\documentclass{article}");
					newLine();
					write("\\begin{document}");
					newLine();
				}
				//TODO outcomment 
				//write("\\twocolumn");
				newLine();
				newLine();
				for (Category cat : Category.values()) {
					if (cat2funs.get(cat) != null) {
						generateCategoryDocs(cat);
					}
				}
				if (STANDALONE) {
					write("\\end{document}");
					newLine();
				}
			} finally {
				bw.close();
			}
		}

		private void write(String... strings) throws IOException {
			for (String s : strings) {
				bw.write(s);
			}
		}

		private void newLine() throws IOException {
			bw.newLine();
		}

		private void generateCategoryDocs(Category cat) throws IOException {
			String heading = cat.toString().toLowerCase().replace('_', ' ');
			heading = heading.substring(0, 1).toUpperCase()
					.concat(heading.substring(1));
			newLine();
			write("\\subsection{" + heading + "}");
			newLine();

			List<AnnotationInfo> funs = cat2funs.get(cat);
			for (AnnotationInfo e : funs) {
				generateFunctionDocs(e);
			}
		}

		private void generateFunctionDocs(AnnotationInfo info)
				throws IOException {
			newLine();
			write("\\paragraph*{" + info.name + ".}");
			newLine();
			write(info.description);
			newLine();
		
			generateSignatures(info);

			newLine();
		}

		private void generateSignatures(AnnotationInfo info)
				throws IOException {
			write("\\begin{itemize}");

			for (Signature sig : info.signatures) {
				write("\\item $" +info.name + ": ");
				for (int i = 0; i < sig.parameterTypes.length; i++) {
					if (i != 0) {
						write(" \\times ");
					}
					write(info.params[i]+ " : ");
					write(Types.getGreqlTypeName(sig.parameterTypes[i]));
				}
				write(" \\longrightarrow ");
				write(Types
						.getGreqlTypeName(sig.evaluateMethod.getReturnType()));
				write("$");
			}

			write("\\end{itemize}");
		}
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out
					.println("Generate a LaTeX documentation for all known GReQL functions.");
			System.out.println("Usage: java FunLib /path/to/fundocs.tex");
		} else {
			generateLaTeXFunctionDocs(args[0]);
		}
	}
}
