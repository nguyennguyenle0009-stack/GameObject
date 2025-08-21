package game.entity.attributes;

import java.util.EnumMap;
import java.util.Map;

import game.enums.Attr;

public class Attributes {
	private EnumMap<Attr, Integer> stats = new EnumMap<>(Attr.class);
	public int get(Attr k) { return stats.getOrDefault(k, 0); }
	public void set(Attr k, int v) { stats.put(k, Math.max(0, v)); }
	public void add(Attr k, int d) { set(k, get(k) + d); }
	
	public EnumMap<Attr, Integer> getStarts() { return stats; }
	public Attributes setStarts(EnumMap<Attr, Integer> starts) { this.stats = starts; return this; }

	public Map<Attr, Integer>view() { return Map.copyOf(stats); }
}
