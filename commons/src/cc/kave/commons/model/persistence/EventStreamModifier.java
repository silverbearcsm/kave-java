package cc.kave.commons.model.persistence;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cc.kave.commons.mining.reader.FileReader;

public class EventStreamModifier {

	private File rootFolder;
	private FileReader reader;

	@Inject
	public EventStreamModifier(@Named("eventStream") File directory, FileReader reader) {
		assertTrue(directory.exists(), "Event Stream folder does not exist");
		assertTrue(directory.isDirectory(), "Event Stream folder is not a folder, but a file");
		this.rootFolder = directory;
		this.reader = reader;
	}

	public void modify() {
		File filePath = getReaderPath();
		List<String> lines = reader.readFile(filePath);

		StringBuilder strbuild = new StringBuilder();

		for (String line : lines) {
			String[] lineValues = line.split(",");
			int eventId = Integer.parseInt(lineValues[0]);
			strbuild.append(++eventId + "," + lineValues[1] + "\n");
		}
		String content = strbuild.toString();
		try {
			FileUtils.writeStringToFile(getWriterPath(), content, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getReaderPath() {
		String fileName = rootFolder.getAbsolutePath() + "/eventstream.txt";
		File file = new File(fileName);
		return file;
	}

	private File getWriterPath() {
		String fileName = rootFolder.getAbsolutePath() + "/eventstreamModified.txt";
		File file = new File(fileName);
		return file;
	}
}