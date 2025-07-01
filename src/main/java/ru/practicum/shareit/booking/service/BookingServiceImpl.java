package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Collection;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoResponse;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.util.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private static final Sort SORT_BY_START_DATE_DESC = Sort.by(Sort.Direction.DESC, "startDate");
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;



    @Override
    @Transactional
    public BookingDtoResponse createBooking(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId)));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format(ITEM_NOT_FOUND_ERR, bookingDto.getItemId())));

        if (Boolean.FALSE.equals(item.getAvailable())) throw new ValidationException(ITEM_NOT_AVAILABLE);

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
            throw new ValidationException(NOT_OWNER_UPDATE_MESSAGE);
        }

        BookingStatus status = approved ? APPROVED : REJECTED;
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        return toBookingDtoResponse(updatedBooking);
    }

    @Override
    public BookingDtoResponse getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format(BOOKING_NOT_FOUND_ERR, bookingId)));

        if (!isBookerOrOwner(booking, userId)) {
            throw new ValidationException(BOOKER_OR_OWNER_ACCESS_REQUIRED);
        }

        return toBookingDtoResponse(booking);
    }

    @Override
    public Collection<BookingDtoResponse> getUserBookings(Long userId, String state) {
        checkUserExists(userId);
        BookingState bookingState = parseState(state);
        Collection<Booking> bookings = findBookingsByStatus(userId, bookingState, false);
        return mapToDtoResponse(bookings);
    }

    @Override
    public Collection<BookingDtoResponse> getOwnerBookings(Long userId, String state) {
        checkUserExists(userId);
        BookingState bookingState = parseState(state);
        Collection<Booking> bookings = findBookingsByStatus(userId, bookingState, true);
        return mapToDtoResponse(bookings);
    }

    private Collection<Booking> findBookingsByStatus(Long userId, BookingState state, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, now, now, SORT_BY_START_DATE_DESC)
                    : bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfter(userId, now, now, SORT_BY_START_DATE_DESC);
            case PAST -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndEndDateIsBefore(userId, now, SORT_BY_START_DATE_DESC)
                    : bookingRepository.findByBookerIdAndEndDateIsBefore(userId, now, SORT_BY_START_DATE_DESC);
            case FUTURE -> isOwner
                    ? bookingRepository.findByItemOwnerIdAndStartDateIsAfter(userId, now, SORT_BY_START_DATE_DESC)
                    : bookingRepository.findByBookerIdAndStartDateIsAfter(userId, now, SORT_BY_START_DATE_DESC);
            case WAITING, REJECTED -> {
                BookingStatus status = state == BookingState.WAITING ? BookingStatus.WAITING : REJECTED;
                yield isOwner
                        ? bookingRepository.findByItemOwnerIdAndStatus(userId, status, SORT_BY_START_DATE_DESC)
                        : bookingRepository.findByBookerIdAndStatus(userId, status, SORT_BY_START_DATE_DESC);
            }
            case ALL -> isOwner
                    ? bookingRepository.findByItemOwnerId(userId, SORT_BY_START_DATE_DESC)
                    : bookingRepository.findByBookerId(userId, SORT_BY_START_DATE_DESC);
        };
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException(String.format(USER_NOT_FOUND_ERR, userId));
    }

    private BookingState parseState(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BookingState.ALL;
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