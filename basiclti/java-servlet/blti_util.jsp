<%!
  // Setup some fake data from the LMS
  private Properties getLMSDummyData(Properties postProp) {
    postProp.setProperty("resource_link_id","120988f929-274612");
    postProp.setProperty("user_id","292832126");
    postProp.setProperty("roles","Instructor");
    postProp.setProperty("lis_person_name_full","Jane Q. Public");
    postProp.setProperty("lis_person_contact_email_primary","user@school.edu");
    postProp.setProperty("lis_person_sourcedid","school.edu:user");
    postProp.setProperty("context_id","456434513");
    postProp.setProperty("context_title","Design of Personal Environments");
    postProp.setProperty("context_label","SI182");
    return postProp;
  }
%>
