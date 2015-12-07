package cc.kave.commons.mining.reader;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cc.kave.commons.model.episodes.Episode;

public class EventStreamAsListOfMethodsParser {

	private File rootFolder;
	private FileReader reader;

	@Inject
	public EventStreamAsListOfMethodsParser(@Named("events") File directory, FileReader reader) {
		assertTrue(directory.exists(), "Event stream folder does not exist!");
		assertTrue(directory.isDirectory(), "Event stream folder is not a folder, but a file!");
		this.rootFolder = directory;
		this.reader = reader;
	}

	public List<Episode> parse() {
		List<String> eventStream = reader.readFile(getFilePath());
		List<Episode> methodsStream = new LinkedList<Episode>();
		List<String> facts = new LinkedList<String>();
		double previousEventTimestamp = 0.000;

		for (String line : eventStream) {
			String[] rowValues = line.split(",");
			double currentEventTimestamp = Double.parseDouble(rowValues[1]);
			if ((currentEventTimestamp - previousEventTimestamp) >= 0.5) {
				Episode method = createMethod(facts);
				methodsStream.add(method);
				facts = new LinkedList<String>();
			}
			if (!facts.contains(rowValues[0])) {
				facts.add(rowValues[0]);
			}
			previousEventTimestamp = currentEventTimestamp;
		}
		Episode lastMethod = createMethod(facts);
		methodsStream.add(lastMethod);
		return methodsStream;
	}

	private Episode createMethod(List<String> events) {
		Episode episode = new Episode();
		episode.setFrequency(1);
		episode.setNumEvents(events.size());
		episode.addListOfFacts(events);
		for (int idx = 0; idx < (events.size() - 1); idx++) {
			episode.addFact(events.get(idx) + ">" + events.get(idx + 1));
		}
		return episode;
	}

	private File getFilePath() {
		String fileName = rootFolder.getAbsolutePath() + "/eventstreamModified.txt";
		File file = new File(fileName);
		return file;
	}
}