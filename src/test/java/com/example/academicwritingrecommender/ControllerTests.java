package com.example.academicwritingrecommender;

import com.google.common.collect.Lists;
import com.shitajimado.academicwritingrecommender.controller.IndexController;
import com.shitajimado.academicwritingrecommender.controller.api.*;
import com.shitajimado.academicwritingrecommender.entities.*;
import com.shitajimado.academicwritingrecommender.entities.dtos.CorpusDto;
import com.shitajimado.academicwritingrecommender.entities.dtos.DocumentDto;
import com.shitajimado.academicwritingrecommender.entities.dtos.UserDto;
import com.shitajimado.academicwritingrecommender.services.CorpusService;
import com.shitajimado.academicwritingrecommender.services.DocumentService;
import com.shitajimado.academicwritingrecommender.services.UserService;
import org.apache.tomcat.util.file.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorpusApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CorpusService service;

	public static final String name = "default";
	public static final CorpusDto corpusDto = new CorpusDto(name);
	public static final Corpus corpus = new Corpus(corpusDto);

	@Test
	void testCreate() throws Exception {
		when(service.createCorpus(corpusDto)).thenReturn(corpus);

		this.mockMvc.perform(post("/create_corpus"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name));
	}

	@Test
	void testRead() throws Exception {
		when(service.readCorpora()).thenReturn(Collections.singletonList(corpus));

		this.mockMvc.perform(get("/read_corpus"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.0.name").value(name));
	}
}

@WebMvcTest(DocumentApiController.class)
class DocumentApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CorpusService service;

	public static final List<DocumentDto> documentDtos = Arrays.asList(
			new DocumentDto("", "fst", "1234", "13.04.2021"),
			new DocumentDto("", "snd", "2435", "09.05.2020"),
			new DocumentDto("", "trd", "3456", "07.07.2019")
	);
	public static final Document document = new Document(documentDtos.get(0));

	@Test
	void testCreate() throws Exception {
		final Corpus corpus = CorpusApiControllerTests.corpus;
		when(service.createAndAddDocuments(documentDtos)).thenReturn(Arrays.asList(corpus, corpus, corpus));

		this.mockMvc.perform(post("/create_documents"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.0.name").value(CorpusApiControllerTests.name));
	}
}

@WebMvcTest(TextApiController.class)
class TextApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private DocumentService service;

	@Test
	void testRead() throws Exception {
		final Document document = DocumentApiControllerTests.document;
		when(service.readText(document)).thenReturn(new Text());

		this.mockMvc.perform(get("/read_text"))
				.andDo(print())
				.andExpect(status().isOk());
		// .andExpect(MockMvcResultMatchers.jsonPath("$.0.name").value(CorpusApiControllerTests.name));
	}
}

@WebMvcTest(IndexController.class)
class IndexControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserService service;

	@Test
	void testRead() throws Exception {
		final UserDto userDto = new UserDto("user", "password", "password");
		final User user = new User(userDto, new Role("USER"));
		when(service.register(userDto)).thenReturn(user);

		this.mockMvc.perform(get("/register"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userDto.getUsername()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.password").value(userDto.getPassword()));

		when(service.login(userDto)).thenReturn(user);

		this.mockMvc.perform(get("/login"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username").value(userDto.getUsername()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.password").value(userDto.getPassword()));
	}
}

