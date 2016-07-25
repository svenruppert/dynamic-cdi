package junit.org.rapidpm.ddi.scopes.v004;

import org.junit.Assert;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class ServiceImpl implements Service {
  public ServiceImpl() {
  }

  @Inject
  private SingleResource singleResource;

  @PostConstruct
  private void postConstruct() {
    //HikariDataSource dataSource = ((this.pools == null) ? this.pools = new JDBCConnectionPools() : this.pools).getDataSource(poolname);
    if(singleResource == null){
      Assert.fail();
    }
  }

  @Override
  public long value() {
    return singleResource.value;
  }
}
