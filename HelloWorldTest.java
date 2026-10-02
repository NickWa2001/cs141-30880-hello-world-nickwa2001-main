// @author Adrian Veliz

import java.lang.reflect.*;
import java.io.*;
@SuppressWarnings("unchecked")
public class HelloWorldTest{
	public static void main(String[]args){
		try{
			assert false;
			throw new Exception("Asserts not enabled");
		}catch(AssertionError ae){//asserts enabled
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		boolean pass = false;
		String className = "HelloWorld", methodName = "main", params = "String[]", errorMsg = "";
		String fullName = className+"."+methodName+"("+params+")";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream original = System.out, record = new PrintStream(baos);
		System.setOut(record);
		try{
			Class helloWorld = Class.forName(className);
			Method main = null;
			Method[] methods = helloWorld.getDeclaredMethods();
			for(Method m : methods){
				if(m.getName().equals("main")){
					main = m;
					break;
				}
			}
			if(main == null)throw new NoSuchMethodException();
			if(main.getParameterCount() == 1){
				Object[] args2 = new Object[1];
				args2[0] = new String[0];
				try{//static
					main.invoke(null, args2);
				}catch(InvocationTargetException | NullPointerException ite){//non-static
					Constructor con = helloWorld.getDeclaredConstructor(new Class[0]);
					Object hw = helloWorld.cast(con.newInstance());
					main.invoke(hw, args2);
				}
			}else{
				try{//static
					main.invoke(null, new Object[0]);
				}catch(InvocationTargetException | NullPointerException ite){//non-static
					Constructor con = helloWorld.getDeclaredConstructor(new Class[0]);
					Object hw = helloWorld.cast(con.newInstance());
					main.invoke(hw, new Object[0]);
				}
				params = "";
				fullName = className+"."+methodName+"("+params+")";
			}
			record.flush();
			String expected = "Hello World!", actual = baos.toString().trim();
			assert actual.equals(expected) : "Expected <" + expected + "> but was <"+actual+">";
			pass = true;
		}catch(ClassNotFoundException cnfe){
			errorMsg = "Could not find class \""+className+"\"";
		}catch(NoSuchMethodException nsme){
			errorMsg = "Could not find method "+fullName;
		}catch(IllegalArgumentException iae){
			errorMsg = "Illegal argument(s) for method "+fullName;
		}catch(IllegalAccessException iae){
			errorMsg = "Could not access method "+fullName;
		}catch(InvocationTargetException ite){
			errorMsg = "Method "+fullName+" threw an exception: " + ite.getCause();
		}catch(InstantiationException ie){
			errorMsg = "Could not build instance";
		}catch(AssertionError ae){
			errorMsg = ae.getMessage();
		}finally{
			System.setOut(original);
		}
		if(!pass){
			System.out.println("Test failed: "+errorMsg);
			System.exit(-1);
		}
		System.out.println("Test passed: "+fullName);
	}
}
