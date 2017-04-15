package web.application;

public class Query {
	private String name;
	private String link;
	private String sql;
	
	public Query(String name, String link, String sql) {
		super();
		this.name = name;
		this.link = link;
		this.sql = sql;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {
		return "Query [name=" + name + ", link=" + link + ", sql=" + sql + "]";
	}
}
