package amy.fp.web;

import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import java.util.List;

@Controller
@RequestMapping("/index")
public class IndexController {

    @RequestMapping
    public ModelAndView index() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            SAMLCredential credential = (SAMLCredential) authentication.getCredentials();
            System.out.println("==============================================");
            System.out.println("SAMLCredential attributes:");
            if(credential!=null){
                List<Attribute> list = credential.getAttributes();
                if(list!=null){
                    for (Attribute attribute:list){
                        System.out.println(attribute.getName()+"="+credential.getAttributeAsString(attribute.getName()));
                    }
                }
            }

            System.out.println("===============================================");
        }

        ModelAndView model = new ModelAndView(new InternalResourceView("/index.jsp", true));
        return model;

    }

}