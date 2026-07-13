// --- Auth ---
export type UserRole = "car-owner" | "storage-owner";

export interface SignInDto {
  email: string;
  password: string;
}

export interface AuthUserDto {
  role: UserRole;
  user: CarOwnerOutputDto | StorageOwnerOutputDto;
}

// --- User / CarOwner ---

export interface CarOwnerInputDto {
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
}

export interface CarOwnerOutputDto {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

// --- User / StorageOwner ---

export interface LocationDto {
  street: string;
  houseNumber: string;
  postalCode: string;
  city: string;
}

export interface StorageDto {
  conditions: string[];
  capacity: number;
}

export interface StorageOwnerInputDto {
  email: string;
  password?: string;
  companyName: string;
  costs: number;
  serviceStartTime: string;
  serviceEndTime: string;
  location: LocationDto;
  storage: StorageDto;
  carManufacturers: string[];
}

export interface StorageOwnerOutputDto {
  id: string;
  email: string;
  companyName: string;
  costs: number;
  serviceStartTime: string;
  serviceEndTime: string;
  location: LocationDto;
  storage: StorageDto;
  carManufacturers: string[];
}

export interface StorageOwnerSearchResDto {
  id: string;
  companyName: string;
  costs: number;
  serviceStartTime: string;
  serviceEndTime: string;
  location: LocationDto;
  storage: StorageDto;
  carManufacturers: string[];
  services: ServiceOutputDto[];
}

// --- Car ---

export type CarCategory = "YOUNGTIMER" | "OLDTIMER";

export interface CarInputDto {
  licensePlate: string;
  manufacturer: string;
  category: CarCategory | null;
}

export interface CarOutputDto {
  id: string;
  licensePlate: string;
  category: CarCategory | null;
  manufacturer: string | null;
  readyToDrive: boolean;
  isMaintained: boolean;
  bookedServiceIds: string[];
  currentStorageOwnerId: string | null;
  currentStorageOwnerName: string | null;
}

export interface CarStatusDto {
  readyToDrive: boolean;
  isMaintained: boolean;
}

// --- Service ---

export interface ServiceInputDto {
  title: string;
  description: string;
  price: number;
  isOptional: boolean;
}

export interface ServiceOutputDto {
  id: string;
  title: string;
  description: string;
  price: number;
  isOptional: boolean;
}

// --- Request ---

export type RequestType = "STELLPLATZ";

export interface RequestInputDto {
  storageOwnerId: string;
  description: string;
}

export interface RequestOutputDto {
  id: string;
  type: RequestType;
  description: string;
  storageOwnerId: string;
  carOwnerId: string | null;
}

// --- Offer ---

export interface OfferInputDto {
  description: string;
  costs: number;
}

export interface OfferOutputDto {
  id: string;
  description: string;
  costs: number;
  accepted: boolean | null;
  requestId: string;
  storageOwnerId: string;
  storageOwnerName: string;
  carOwnerId: string | null;
}

export interface OfferResponseDto {
  accepted: boolean;
}

// --- Appointment ---

export type AppointmentType = "EINLAGERUNG" | "AUSLAGERUNG";

export type AppointmentStatus = "AVAILABLE" | "BOOKED" | "COMPLETED";

export interface AppointmentSlotInputDto {
  type: AppointmentType;
  date: string;
  time: string;
}

export interface AppointmentBookingInputDto {
  carId: string;
  type: AppointmentType;
  date: string;
  time: string;
}

export interface AppointmentOutputDto {
  id: string;
  type: AppointmentType;
  status: AppointmentStatus;
  date: string;
  time: string;
  storageOwnerId: string;
  carId: string | null;
}
