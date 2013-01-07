package common.main.data.filter;

import java.util.ArrayList;

import common.main.JGAction;

public class JGActionFilter {
	public interface JGActionFilterItem{
		public void doFilter(JGAction action_);
	}
	
	private ArrayList<JGActionFilterItem> _filterListF = new ArrayList<JGActionFilterItem>();
	private ArrayList<JGActionFilterItem> _filterListL = new ArrayList<JGActionFilterItem>();
	
	public ArrayList<JGActionFilterItem> getFilterListF(){
		return _filterListF;
	}
	public ArrayList<JGActionFilterItem> getFilterListL(){
		return _filterListL;
	}
	
	public void doFilterF(JGAction action_){
		int count_ = _filterListF.size();
		for(int index_=0;index_<count_;++index_){
			_filterListF.get(index_).doFilter(action_);
		}
	}
	public void doFilterL(JGAction action_){
		int count_ = _filterListL.size();
		for(int index_=0;index_<count_;++index_){
			_filterListL.get(index_).doFilter(action_);
		}
	}
	
	public void addFilterF(JGActionFilterItem item_){
		_filterListF.add(item_);
	}
	public void removeFilterFAtIndex(int index_){
		_filterListF.remove(index_);
	}
	public void removeFilterF(JGActionFilterItem item_){
		removeFilterFAtIndex(indexOfFilterF(item_));
	}
	public JGActionFilterItem filterFAtIndex(int index_){
		return _filterListF.get(index_);
	}
	public int indexOfFilterF(JGActionFilterItem item_){
		return _filterListF.indexOf(item_);
	}
	
	public int countOfFilterF(){
		return _filterListF.size();
	}
	
	public void addFilterL(JGActionFilterItem item_){
		_filterListL.add(item_);
	}
	public void removeFilterLAtIndex(int index_){
		_filterListL.remove(index_);
	}
	public void removeFilterL(JGActionFilterItem item_){
		removeFilterLAtIndex(indexOfFilterL(item_));
	}
	public JGActionFilterItem filterLAtIndex(int index_){
		return _filterListL.get(index_);
	}
	public int indexOfFilterL(JGActionFilterItem item_){
		return _filterListL.indexOf(item_);
	}
	
	public int countOfFilterL(){
		return _filterListL.size();
	}
}
