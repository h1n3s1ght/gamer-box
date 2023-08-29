package com.benhines.inventoryservice;

import com.benhines.inventoryservice.model.Inventory;
import com.benhines.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}


	@Bean
	public CommandLineRunner loadData(InventoryRepository repository){
		return args -> {
			checkAndSaveInventory(repository, "basic_package", 1000);
			checkAndSaveInventory(repository, "middle_package", 25);
			checkAndSaveInventory(repository, "elite_package", 2);
			checkAndSaveInventory(repository, "extreme_package", 0);
		};
	}

	private void checkAndSaveInventory(InventoryRepository repository, String skuCode, Integer quantity) {
		Optional<Inventory> exists = repository.findBySkuCode(skuCode);
			if (exists.isEmpty()) {
				Inventory inventory = new Inventory();
				inventory.setSkuCode(skuCode);
				inventory.setQuantity(quantity);
				repository.save(inventory);
			}
		}
	}

