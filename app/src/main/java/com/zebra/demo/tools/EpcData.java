package com.zebra.demo.tools;
import java.io.Serializable;

public class EpcData implements Serializable {

	 private String _code = "-";
     private String _name = "-";
     private String _model = "-";
     private String _count = "-";
     private String _batch = "-";
     private String _box = "-";
     private String _type = "-";
     private String _createTime = "-";
     private String _createCompany = "-";
     private String _lsm = "";
     
	public String get_lsm() {
		return _lsm;
	}
	public void set_lsm(String _lsm) {
		this._lsm = _lsm;
	}
	public String get_code() {
		return _code;
	}
	public void set_code(String _code) {
		this._code = _code;
	}
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
	}
	public String get_model() {
		return _model;
	}
	public void set_model(String _model) {
		this._model = _model;
	}
	public String get_count() {
		return _count;
	}
	public void set_count(String _count) {
		this._count = _count;
	}
	public String get_batch() {
		return _batch;
	}
	public void set_batch(String _batch) {
		this._batch = _batch;
	}
	public String get_box() {
		return _box;
	}
	public void set_box(String _box) {
		this._box = _box;
	}
	public String get_type() {
		return _type;
	}
	public void set_type(String _type) {
		this._type = _type;
	}
	public String get_createTime() {
		return _createTime;
	}
	public void set_createTime(String _createTime) {
		this._createTime = _createTime;
	}
	public String get_createCompany() {
		return _createCompany;
	}
	public void set_createCompany(String _createCompany) {
		this._createCompany = _createCompany;
	}
     
     
}
