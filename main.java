import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class main {

	public static HashMap<String, String> letterLookup;
	public static HashMap<String, String> statesSet;

	public static void main(String[] args) throws FileNotFoundException {
		File inputFile = new File(args[0]);
		try {
			String encodedTM = "";
			String notCodedδ = "";
			Scanner sc = new Scanner(inputFile);
			letterLookup = new HashMap<>();
			statesSet = new HashMap<>();
			String initialState = "";
			String finalStates = "";
			HashMap<String, String> translations = new HashMap<String, String>();
			HashMap<String, String> readTot = new HashMap<String, String>();

			while (sc.hasNextLine()) {
				String i = sc.nextLine();
				// TODO: Capture final and initial states, make initial state first transition
				// Capture and encode initial and final states
				if (i.contains("block id=")) {
					String stateId = i.substring(i.indexOf("\"") + 1, i.indexOf("name") - 2);
					stateId = stateToUnary(stateId);
					// scan until end of block
					while (!i.contains("/block")) {
						i = sc.nextLine();
						if (i.contains("initial")) {
							initialState = stateId;
						}
						if (i.contains("final")) {
							if (finalStates.length() == 0)
							{
								finalStates += stateId;
							}
							else
							{
								finalStates += 0 + stateId;
							}
						}
					}
				}
				// Capture and encode transitions
				else if (i.contains("<transition")) {
					String fromState = sc.nextLine();
					String before = "";
					String toState = sc.nextLine();
					String to = "";
					String read = sc.nextLine();
					String bRead = "";
					String write = sc.nextLine();
					String bWrite = "";
					String move = sc.nextLine();
					String bMove = "";

					fromState = getStringBetweenDiv(fromState);
					before = fromState;
					toState = getStringBetweenDiv(toState);
					to = toState;
					read = getStringBetweenDiv(read).toLowerCase();
					bRead = read;
					write = getStringBetweenDiv(write).toLowerCase();
					bWrite = write;
					move = getStringBetweenDiv(move);
					bMove = move;

					fromState = stateToUnary(fromState);
					toState = stateToUnary(toState);

					read = symbolToUnary(read);
					write = symbolToUnary(write);
					
					if (move.equals("R"))
					{
						move = "11";
					}
					else if(move.equals("L"))
					{
						move = "1";
					}
					else if(move.equals("S"))
					{
						move = "111";
					}				

					String encodedTransition = "D" + fromState + 0 + read + 0 + toState +
							0 + write + 0 + move;

					if (bRead.equals("empty"))
					{
						bRead = "□";
					}
					if (bWrite.equals("empty"))
					{
						bWrite = "□";
					}
					readTot.put(bRead, read);
					String δ = "δ(q" + before + ", " + bRead + ") = (q" + to + ", " + bWrite + ", " + bMove + "), ";
					translations.put(δ, encodedTransition);
					// add encoded transition to start of string if it's initial
					if (fromState.equals(initialState))
					{
						encodedTM = encodedTransition + encodedTM;
						notCodedδ += δ;
					}
					else
					{
						encodedTM += encodedTransition;
						notCodedδ += δ;
					}             
				}
			}

			encodedTM += "F" + finalStates;
			System.out.println(encodedTM);
			sc.close();
			String print = "";
			System.out.print("LANGUAGE ALPHABET: ");
			for (String name : letterLookup.keySet())
			{
				String key = name.toString();
				String value = letterLookup.get(name).toString();
				print = print + key + ": " + value + ", ";
			}
			System.out.println(print);

			String[] parts = finalStates.split("0");
			String fstates = "";
			for (int i = 0; i < parts.length; i++)
			{
				String hold = "";
				for(String name : statesSet.keySet())
				{
					if(parts[i].equals(statesSet.get(name).toString()))
					{
						hold = name;
						break;
					}
				}
				fstates = fstates + "q" + hold + ", " + parts[i] + " ";
			}

			String q0 = "q";
			String q = "{";
			String v = "{";
			int count = 0;
			for (String name : statesSet.keySet())
			{
				String key = name.toString();
				String value = statesSet.get(name).toString();
				if (count == 0)
				{
					q0 = q0 + key + ", " + statesSet.get(name).toString();
				}
				if(count == statesSet.size() - 1)
				{
					q = q + "q" + key;
					v = v + value;
				}
				else
				{
					q = q +"q" + key + ", ";
					v = v + value + ", ";
				}
				count++;
			}


			int counter = 0;
			String total = "{";
			String encodedTot = "{";
			for (String name : readTot.keySet())
			{
				String key = name.toString();
				String value = readTot.get(name).toString();
				if(counter == readTot.size() - 1)
				{
					total = total + key + "}";
					encodedTot = encodedTot + value + "}";
				}
				else
				{
					total = total + key +", ";
					encodedTot = encodedTot + value + ", ";
				}
				counter++;
			}

			String combo = "";
			int c = 0;
			for (String name : translations.keySet())
			{
				String key = name.toString();
				String value = translations.get(name).toString();
				if(c == translations.size() - 1)
				{
					combo = combo + key + value;
				}
				else
				{
					combo = combo + key + value + " | ";
				}
			}
			String k = q + "}, " + v + "}";
			String j = total + ", " + encodedTot;
			System.out.println(notCodedδ + "\t" + fstates + "\t" + k + "\t" + q0 + "\t" + j + "\t" + combo);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String symbolToUnary(String inputSymbol) {
		// handle blank
		if (inputSymbol.equals("empty")) {
			return "1";
		}
		if (!letterLookup.containsKey(inputSymbol)) {
			// create key
			letterLookup.put(inputSymbol, stateToUnary(Integer.toString(letterLookup.size())) + 1);
		}
		return letterLookup.get(inputSymbol);
	}

	private static String getStringBetweenDiv(String input) {
		String returnValue;
		// blank (square) input
		if (input.contains("/>")) {
			returnValue = "empty";
		} else {
			// extract value between > and < if not a blank div
			returnValue = input.substring(input.indexOf(">") + 1, input.lastIndexOf("<"));
		}
		return returnValue;
	}

	private static String stateToUnary(String input) {
		String unaryOutput = "";
		for (int i = 0; i <= Integer.parseInt(input); i++) {
			unaryOutput += "1";
		}
		statesSet.put(input, unaryOutput);
		return unaryOutput;
	}
}