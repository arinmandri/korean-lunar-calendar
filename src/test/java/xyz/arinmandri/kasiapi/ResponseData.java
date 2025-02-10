package xyz.arinmandri.kasiapi;

import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "response", strict = false)
@lombok.Data
public class ResponseData {

	@Element(name = "body")
	private Body body;

	@Root(name = "body", strict = false)
	@lombok.Data
	public static class Body {

		@Element(name = "items")
		private Items items;
	}

	@Root(name = "items", strict = false)
	@lombok.Data
	public static class Items {

		@ElementList(name = "item", inline = true, required = false, empty = true)
		private List<Item> itemList;

		public List<Item> getItemList() {
			// null일 경우 빈 리스트를 반환
			return itemList != null ? itemList : Collections.emptyList();
		}
	}
}
