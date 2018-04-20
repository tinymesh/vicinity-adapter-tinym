package com.tinymesh.vicinity.adapter.controller;

import com.tinymesh.vicinity.adapter.entity.Device;
import com.tinymesh.vicinity.adapter.repository.DeviceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
public class ObjectsApiControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private DeviceRepository deviceRepository;

    @Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

        HttpMessageConverter mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

		assertNotNull("the JSON message converter must not be null",
                mappingJackson2HttpMessageConverter);
	}

	//Testing if object has same values as Device
	@Test
	public void getAllObjects() throws Exception {
		List<Device> deviceList =  deviceRepository.findAll();

        mockMvc.perform(get("/objects"))
				.andExpect(status().isOk())
                .andExpect(jsonPath("$", samePropertyValuesAs(ObjectsApiController.mapDeviceDataToObjectInfo(deviceList))))
                .andDo(print());
	}

    /**
     * Checking if status OK when we GET from Object Property
     * Also checks that Last-Modified Header is present
     */
    @Test
    public void getObjectPropertyCheckGetStatusOK() throws Exception{
		List<Device> deviceList = deviceRepository.findAll();
		mockMvc.perform(get("/objects/{oid}/properties/state", deviceList.get(0).getUuid()))
				.andExpect(status().isOk())
                .andExpect(header().exists("Last-Modified"))
				.andDo(print());
	}

    /**
     * Checking if status OK when we GET from Object Property
     * Also checks that Last-Modified Header is not present when state is null
     */
    @Test
    public void getObjectPropertyReturnsExpectedResultWhenStateIsNullTest() throws Exception{
        Device fakeDevice = new Device(
                "Fake Device",
                "FakeDeviceType1",
                UUID.randomUUID(),
                LocalDateTime.now(ZoneId.of("UTC")),
                null,
                "www.test.com",
                123
        );

        deviceRepository.save(fakeDevice);

        mockMvc.perform(get("/objects/{oid}/properties/state", fakeDevice.getUuid()))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Last-Modified"))
                .andDo(print());
    }

    /**
     * Checking if Status NOT FOUND when we GET with wrong UUID from Object Property
     */
    @Test
	public void checkGetStatusNOT_FOUND() throws Exception{
		mockMvc.perform(get("/objects/{uuid}/properties/state", UUID.randomUUID()))
				.andExpect(status().isNotFound())
				.andDo(print());
	}

    /**
     *
     * @throws Exception
     */
    @Test
    public void checkGetStatusNOT_FOUNDWhenInvalidPid() throws Exception{
        List<Device> deviceList = deviceRepository.findAll();
        mockMvc.perform(get("/objects/{uuid}/properties/status", deviceList.get(0).getUuid()))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}