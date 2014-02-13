package org.weibocontentlib.api.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.weibocontentlib.entity.Operator;
import org.weibocontentlib.service.OperatorService;
import org.weibocontentlib.service.exception.ServiceException;

public class OperatorDetailsService implements UserDetailsService {

	private OperatorService operatorService;

	public void setOperatorService(OperatorService operatorService) {
		this.operatorService = operatorService;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		final Operator operator;

		try {
			operator = operatorService.getOperatorByName(username);
		} catch (ServiceException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}

		return new UserDetails() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public boolean isCredentialsNonExpired() {
				return true;
			}

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}

			@Override
			public boolean isAccountNonExpired() {
				return true;
			}

			@Override
			public String getUsername() {
				return operator.getName();
			}

			@Override
			public String getPassword() {
				return operator.getPassword();
			}

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				GrantedAuthority operatorRoleGrantedAuthority = new SimpleGrantedAuthority(
						operator.getRole());

				Collection<GrantedAuthority> grantedAuthorityList = new ArrayList<GrantedAuthority>();
				grantedAuthorityList.add(operatorRoleGrantedAuthority);

				return grantedAuthorityList;
			}

		};
	}

}
