/*
 *  Copyright 2018, Mindtree Ltd. - All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package blockchain.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;


/**
 * 
 * @author SWATI RAJ
 *
 */


/**
 * 
 * class for generationg JWT token in the starting
 *
 */
public class JwtFilter extends GenericFilterBean {
    public static String uname;
	
	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException
			 {
        
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		final String authHeader = request.getHeader("authorization");
		

		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);

			chain.doFilter(req, res);
		} else {

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				throw new ServletException("Missing or invalid Authorization header");
			}

			final String token = authHeader.substring(7);

			try {
				final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
				
				uname=claims.getSubject();
				request.setAttribute("claims", claims);
			} catch (Exception e) {
				System.out.println("Invalid Token");
				e.printStackTrace();
				
			}

			chain.doFilter(req, res);
		}
	}
}
