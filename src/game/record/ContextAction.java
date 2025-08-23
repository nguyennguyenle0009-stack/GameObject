package game.record;

import game.interfaces.ContextPerformer;

public record ContextAction(String label, ContextPerformer fn) {
	
}
