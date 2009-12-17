/*****************************************************************************
 * Copyright (c) 2009
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com>
 *     Jawher Moussa <jawher.moussa@gmail.com>
 *     Nicolas Inchauspe <nicolas.inchauspe@gmail.com>
 *     Pascal Leclercq <pascal.leclercq@gmail.com>
 *******************************************************************************/
package org.dynaresume.hr.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import org.dynaresume.hr.repository.ResumeRepository;
import org.dynaresume.hr.domain.Resume;
import org.dynaresume.hr.service.HRService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service(value="hrService")
public class HRServiceImpl implements HRService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(HRServiceImpl.class);

	@Autowired
	private ResumeRepository resumeRepository;

	public List<Resume> findAll() {
		logger.debug("findAll() - start"); //$NON-NLS-1$
		

		List<Resume> returnList = resumeRepository.findAll();
		logger.debug("findAll() - end"); //$NON-NLS-1$
		return returnList;
	}

}