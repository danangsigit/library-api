package test.technical.librarian.utils;

import test.technical.librarian.constant.PssConstant;
import test.technical.librarian.dto.request.GlobalFilter;
import test.technical.librarian.dto.request.PssFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ControllerHelper {

	public static PssFilter buildSelect2Filter(String q, Integer page, Integer sortIdx, String sortDir) {
		PssFilter filter = new PssFilter();
		filter.setLength(10);
		filter.setStart(filter.getLength() * (page - 1));
		
		HashMap<String, String> order = new HashMap<>();
		order.put(PssConstant.PSS_ORDER_COLUMN, sortIdx.toString());
		order.put(PssConstant.PSS_ORDER_DIRECTION, sortDir);
		filter.setOrder(Arrays.asList(order));
		
		HashMap<String, String> search = new HashMap<>();
		search.put(PssConstant.PSS_SEARCH_VAL, q);
		search.put(PssConstant.PSS_SEARCH_REG, "false");
		filter.setSearch(search);
		return filter;
	}
	
	public static PssFilter buildGlobalFilter(Integer page, Integer length, Integer sortIdx,
                                              String sortDir, List<GlobalFilter> filters) {
		PssFilter filter = new PssFilter();
		filter.setLength(length);
		filter.setStart(filter.getLength() * (page - 1));
		
		HashMap<String, String> order = new HashMap<>();
		order.put(PssConstant.PSS_ORDER_COLUMN, sortIdx.toString());
		order.put(PssConstant.PSS_ORDER_DIRECTION, sortDir);
		filter.setOrder(Arrays.asList(order));
		
		List<HashMap<String, Object>> columns = new ArrayList<HashMap<String, Object>>();
		for (GlobalFilter gf : filters) {
			HashMap<String, Object> col = new HashMap<>();
			col.put(PssConstant.PSS_DATA, gf.getField());
			col.put(PssConstant.PSS_COL_SEARCH_VAL, gf.getValue());
			columns.add(col);
		}
		filter.setColumns(columns);
		
		filter.setSearch(new HashMap<>());
		return filter;
	}
	
	public static void setColumnFilter(PssFilter filter, List<GlobalFilter> filters) {
		List<HashMap<String, Object>> columns = new ArrayList<HashMap<String, Object>>();
		for (GlobalFilter gf : filters) {
			HashMap<String, Object> col = new HashMap<>();
			col.put(PssConstant.PSS_DATA, gf.getField());
			col.put(PssConstant.PSS_COL_SEARCH_VAL, gf.getValue());
			columns.add(col);
		}
		filter.setColumns(columns);
	}
	
}
