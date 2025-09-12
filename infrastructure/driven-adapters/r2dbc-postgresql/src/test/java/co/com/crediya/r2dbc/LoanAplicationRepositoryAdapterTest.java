package co.com.crediya.r2dbc;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class LoanAplicationRepositoryAdapterTest {

    @InjectMocks
    LoanAplicationRepositoryAdapter repositoryAdapter;

    @Mock
    LoanAplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;
    
}
