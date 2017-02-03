package cc.kave.episodes.preprocessing;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.episodes.io.EventStreamIo;
import cc.kave.episodes.io.ValidationDataIO;
import cc.kave.episodes.model.events.Event;
import cc.kave.episodes.model.events.EventKind;
import cc.recommenders.io.Logger;

import com.google.common.collect.Sets;

public class OverlapingTypes {

	private EventStreamIo trainStreamIo;
	private ValidationDataIO valStream;

	@Inject
	public OverlapingTypes(@Named("events") File folder,
			EventStreamIo streamIo, ValidationDataIO validation) {
		this.trainStreamIo = streamIo;
		this.valStream = validation;
	}

	public Set<ITypeName> getOverlaps(int frequency) {
		List<Event> trainCtx = trainStreamIo.readMethods(frequency);
		Set<ITypeName> trainTyes = Sets.newLinkedHashSet();
		Set<ITypeName> valTypes = Sets.newLinkedHashSet();

		for (Event ctx : trainCtx) {
			if (!ctx.getMethod().equals(Names.getUnknownMethod())) {
				try {
					trainTyes.add(ctx.getMethod().getDeclaringType());
				} catch (Exception e) {
				}
			}
		}
		List<Event> valData = valStream.read(frequency);

		for (Event event : valData) {
			if ((event.getKind() == EventKind.METHOD_DECLARATION)
					&& (!event.getMethod().equals(Names.getUnknownMethod()))) {
				try {
					valTypes.add(event.getMethod().getDeclaringType());
				} catch (Exception e) {
				}
			}
		}

		Set<ITypeName> overlaps = getSetOverlaps(trainTyes,
				valTypes);
		for (ITypeName type : overlaps) {
			Logger.log("Type name: %s", type);
			Logger.log("Namespace: %S", type.getNamespace());
			Logger.log("");
		}
		Logger.log("Number of overlaping namespaces are: %d", overlaps.size());
		Logger.log("Number of types in training stream: %d", trainTyes.size());
		Logger.log("Number of types in validation stream: %d", valTypes.size());
		
		return overlaps;
	}

	private Set<ITypeName> getSetOverlaps(Set<ITypeName> set1,
			Set<ITypeName> set2) {
		Set<ITypeName> results = Sets.newLinkedHashSet();

		for (ITypeName namespace : set1) {
			if (set2.contains(namespace)) {
				results.add(namespace);
			}
		}
		return results;
	}
}