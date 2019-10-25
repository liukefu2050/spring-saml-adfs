package org.springframework.security.saml.web;

import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import java.util.List;

public class TestUserDetails implements SAMLUserDetailsService {
    @Override
    public Object loadUserBySAML(SAMLCredential cred) throws UsernameNotFoundException {
        System.out.println("==========================================");

        List<Attribute> list = cred.getAttributes();
        for (Attribute attribute:list){
            System.out.println(attribute.getName()+"="+cred.getAttributeAsString(attribute.getName()));
        }
        System.out.println();
        System.out.println("==========================================");
        return cred.getAttributeAsString("accountID");
    }
}