package com.shopsphere.inventory.mapper;

import com.shopsphere.inventory.dto.request.CreateInventoryRequest;
import com.shopsphere.inventory.dto.response.InventoryResponse;
import com.shopsphere.inventory.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(CreateInventoryRequest request);

    @Mapping(target = "totalQuantity", ignore = true)
    @Mapping(target = "stockStatus", ignore = true)
    InventoryResponse toResponse(Inventory inventory);
}