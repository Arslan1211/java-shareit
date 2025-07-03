package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoResponse;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.constant.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 0;


    @Override
    @Transactional
    public BookingDtoResponse createBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_ERR, bookingDto.getItemId())));

        if (Boolean.FALSE.equals(item.getAvailable()))
            throw new ValidationException(ITEM_NOT_AVAILABLE);

        Booking booking = toBooking(booker, item, bookingDto);

        Booking savedBooking = bookingRepository.save(booking);
        return toBookingDtoResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingDtoResponse updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_ERR, bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(ONLY_OWNER_CAN_UPDATE_BOOKING_STATUS);
        }

        BookingStatus status = approved ? APPROVED : REJECTED;
        booking.setStatus(status);

        return toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_ERR, bookingId)));

        if (!isBookerOrOwner(booking, userId)) {
            throw new ValidationException(BOOKING_VIEW_PERMISSION_RESTRICTED);
        }

        return toBookingDtoResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoResponse> getUserBookings(Long userId, String state, int from, int size) {
        checkUserExists(userId);
        BookingState bookingState = parseState(state);
        Page<Booking> bookings = findBookingsByStatus(userId, bookingState, false,
                DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
        return mapToDtoResponse(bookings.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoResponse> getOwnerBookings(Long userId, String state, int from, int size) {
        checkUserExists(userId);
        BookingState bookingState = parseState(state);
        Page<Booking> bookings = findBookingsByStatus(userId, bookingState, true,
                DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE);
        return mapToDtoResponse(bookings.getContent());
    }

    private Page<Booking> findBookingsByStatus(Long userId, BookingState state, boolean isOwner,
                                               int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "startDate"));
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(
                    userId, now, now, pageable)
                    : bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(
                    userId, now, now, pageable);
            case PAST -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndEndDateIsBefore(userId, now, pageable)
                    : bookingRepository.findByBookerIdAndEndDateIsBefore(userId, now, pageable);
            case FUTURE -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userId, now, pageable)
                    : bookingRepository.findByBookerIdAndStartDateIsAfter(userId, now, pageable);
            case WAITING -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable)
                    : bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
            case REJECTED -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable)
                    : bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
            case ALL -> isOwner
                    ? bookingRepository.findByItemOwnerId(userId, pageable)
                    : bookingRepository.findByBookerId(userId, pageable);
        };
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format(UNKNOWN_STATE, state, Arrays.toString(BookingState.values())));
        }
    }

    private Collection<BookingDtoResponse> mapToDtoResponse(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .toList();
    }

    private boolean isBookerOrOwner(Booking booking, Long userId) {
        return booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId);
    }
}
