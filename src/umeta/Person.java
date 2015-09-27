package umeta;


public class Person {
	
	static class PersonName{
		private String firstName; 
		private String middleName; 
		private String lastName; 
		
		PersonName(String l, String f, String m){
			firstName = f; 
			middleName = m; 
			lastName = l;
		}
		
		
		public String toString()
		{
			return "<" + lastName+","+ firstName+", "+ middleName + ">" ;
		}
		
		@Override 
		public int hashCode(){
			return lastName.hashCode();
		}
		
		@Override
		public boolean equals(Object name){
			PersonName n = (PersonName) name;
			
			
			if (!lastName.equals(n.getLastName())) return false;
			if ((firstName != null) && (n.getFirstName() != null)){
				if (((firstName.matches("[A-Z]{1}[\\.]{1}")) &&	n.getFirstName().matches("[A-Z]{1}[a-z]+"))
				   || ((n.getFirstName().matches("[A-Z]{1}[\\.]{1}")) &&	firstName.matches("[A-Z]{1}[a-z]+"))){
					
					if (firstName.charAt(0) != n.getFirstName().charAt(0))
						return false;
				} 
				else if (!firstName.equals(n.getFirstName()))
					return false;
			}
			
			if ((middleName != null) && (n.getMiddleName() != null)){
				if (((middleName.matches("[A-Z]{1}[\\.]{1}")) &&	n.getMiddleName().matches("[A-Z]{1}[a-z]+"))
				   || ((n.getMiddleName().matches("[A-Z]{1}[\\.]{1}")) &&	middleName.matches("[A-Z]{1}[a-z]+"))){
					
					if (middleName.charAt(0) != n.getMiddleName().charAt(0))
						return false;
				} 
				else if (!middleName.equals(n.getMiddleName()))
					return false;
			}
			return true;
		}
		
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getMiddleName() {
			return middleName;
		}

		public void setMiddleName(String midName) {
			middleName = midName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lName) {
			lastName = lName;
		}
		
		
	}
	
    PersonName name;
	

	private String uri;
	private String email;
	private StringBuilder keywords = new StringBuilder();
	
	public void print(){
		System.out.println("name :" + name);
		System.out.println("uri :" + uri);
		System.out.println("email :" + email);
		System.out.println("keywords :" + keywords);
	}
	
    public void appendKeywords(String s){
    	if (s != null){
    		if (s.length() > 0)
    			keywords.append("; " +s);
    		else
    			keywords.append(s);
    	}
    }
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getKeywords() {
		return keywords.toString();
	}
	public void setKeywords(String keywords) {
		if (keywords == null) return;
		this.keywords = new StringBuilder();
		this.keywords.append(keywords);
	}

	public PersonName getName() {
		return name;
	}
	public void setName(PersonName name) {
		this.name = name;
	}
	
	
	static public void main (String args[]){
		PersonName test = new PersonName("Koroviev", "N.", "N.");
		PersonName test1 = new PersonName("Koroviev", "N.", "N.");
		PersonName test2 = new PersonName("Koroviev", "Nikolay", "N.");
		PersonName test3 = new PersonName("Koroviev", "N.", "Nikiforovich");
		PersonName test4 = new PersonName("Koroviev", "N.", null);
		PersonName test5 = new PersonName("Koroviev", null, "N.");
		PersonName test6 = new PersonName("Koroviev", null, null);
		
		PersonName test7 = new PersonName("Koroviev", "Koko", "N.");
		PersonName test8 = new PersonName("Koroviev", null, "Koko");
		
		System.out.println(test.getFirstName().charAt(0) );
		System.out.println(test1.getFirstName().charAt(0) );
				
			
		System.out.println(test.equals(test1));
		System.out.println(test.equals(test2));
		System.out.println(test.equals(test3));
		System.out.println(test.equals(test4));
		System.out.println(test.equals(test5));
		System.out.println(test.equals(test6));
		
		
		System.out.println(test4.equals(test5));
		
		System.out.println(test.equals(test7));
		System.out.println(test.equals(test8));
		
	}
}
