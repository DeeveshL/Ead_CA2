using EAD2_CA2_POKEMON.Models;
using Microsoft.EntityFrameworkCore;

public class AppDbContext : DbContext
{
    public DbSet<Cards> Cards { get; set; } = null!;
    public DbSet<Collection> Collections { get; set; } = null!;
    public DbSet<CollectionCard> CollectionCards { get; set; } = null!;

    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<CollectionCard>()
            .HasKey(cc => new { cc.CollectionId, cc.CardId });

        modelBuilder.Entity<CollectionCard>()
            .HasOne(cc => cc.Collection)
            .WithMany(c => c.CollectionCards)
            .HasForeignKey(cc => cc.CollectionId);

        modelBuilder.Entity<CollectionCard>()
            .HasOne(cc => cc.Card)
            .WithMany(c => c.CollectionCards)
            .HasForeignKey(cc => cc.CardId);
    }
}